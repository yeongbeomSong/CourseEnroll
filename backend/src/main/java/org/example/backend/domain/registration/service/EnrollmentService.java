package org.example.backend.domain.registration.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.course.entity.Course;
import org.example.backend.domain.course.repository.CourseRepository;
import org.example.backend.domain.registration.dto.EnrollResult;
import org.example.backend.domain.registration.dto.EnrollStatusResponse;
import org.example.backend.domain.registration.dto.EnrolledStudentResponse;
import org.example.backend.domain.registration.dto.EnrollmentResponse;
import org.example.backend.domain.registration.entity.Registration;
import org.example.backend.domain.registration.repository.RegistrationRepository;
import org.example.backend.domain.registration.service.EnrollQueueService.QueuePosition;
import org.example.backend.domain.student.entity.Student;
import org.example.backend.domain.student.repository.StudentRepository;
import org.example.backend.domain.professor.entity.Professor;
import org.example.backend.domain.professor.repository.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 수강신청·취소·내역. 선착순 요청은 큐에 넣고 스케줄러가 순서대로 처리.
 */
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final RegistrationRepository registrationRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final EnrollQueueService enrollQueueService;

    /** [Student] 수강신청 요청 → 큐에만 넣고 즉시 반환 (실제 처리들은 스케줄러가 순서대로 수행) */
    public EnrollResult enroll(Long studentUserId, Long courseId) {
        Student student = studentRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다."));

        if (registrationRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new IllegalArgumentException("이미 수강신청한 강의입니다.");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        if (!course.getTargetGrade().equals(student.getGrade())) {
            throw new IllegalArgumentException("대상 학년이 아닙니다.");
        }
        int afterCredits = student.getCurrentCredits() + course.getCredit();
        if (afterCredits > student.getMaxCredits()) {
            throw new IllegalArgumentException("신청 가능한 최대 학점을 초과합니다.");
        }

        long position = enrollQueueService.enqueue(courseId, studentUserId);
        return EnrollResult.waitlist(position);
    }

    /** 큐에서 한 건 꺼내 실제 수강신청 처리 (스케줄러에서 호출) */
    @Transactional
    public void processOneFromQueue(Long courseId) {
        Long userId = enrollQueueService.popOne(courseId);
        if (userId == null) return;
        try {
            enrollInternal(userId, courseId);
            enrollQueueService.setResult(userId, courseId, "success");
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            enrollQueueService.setResult(userId, courseId, msg != null && msg.contains("마감") ? "full" : "error");
        } catch (Exception e) {
            enrollQueueService.setResult(userId, courseId, "error");
        }
    }

    /** 실제 DB 수강신청 (정원·중복 등 검사) */
    @Transactional
    public void enrollInternal(Long studentUserId, Long courseId) {
        Student student = studentRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다."));

        if (registrationRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new IllegalArgumentException("이미 수강신청한 강의입니다.");
        }

        Course course = courseRepository.findByIdForUpdate(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        if (course.getCurrentEnrollment() >= course.getCapacity()) {
            throw new IllegalArgumentException("수강 정원이 마감되었습니다.");
        }
        if (!course.getTargetGrade().equals(student.getGrade())) {
            throw new IllegalArgumentException("대상 학년이 아닙니다.");
        }
        int afterCredits = student.getCurrentCredits() + course.getCredit();
        if (afterCredits > student.getMaxCredits()) {
            throw new IllegalArgumentException("신청 가능한 최대 학점을 초과합니다.");
        }

        Registration reg = Registration.builder()
                .student(student)
                .course(course)
                .createdAt(LocalDateTime.now())
                .build();
        registrationRepository.save(reg);

        course.setCurrentEnrollment(course.getCurrentEnrollment() + 1);
        courseRepository.save(course);

        student.setCurrentCredits(student.getCurrentCredits() + course.getCredit());
        studentRepository.save(student);
    }

    /** [Student] 수강신청 상태 조회 (폴링용) */
    public EnrollStatusResponse getEnrollStatus(Long studentUserId, Long courseId) {
        Student student = studentRepository.findByUserId(studentUserId).orElse(null);
        if (student == null) return EnrollStatusResponse.builder().status("error").build();

        if (registrationRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            return EnrollStatusResponse.builder().status("enrolled").build();
        }
        long pos = enrollQueueService.getPosition(courseId, studentUserId);
        if (pos > 0) {
            return EnrollStatusResponse.builder().status("pending").position(pos).build();
        }
        String result = enrollQueueService.getAndClearResult(studentUserId, courseId);
        if (result != null) return EnrollStatusResponse.builder().status(result).build();
        return EnrollStatusResponse.builder().status("none").build();
    }

    /** [Student] 수강신청 취소 */
    @Transactional
    public void cancel(Long studentUserId, Long courseId) {
        Student student = studentRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다."));
        Registration reg = registrationRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new IllegalArgumentException("수강신청 내역을 찾을 수 없습니다."));
        int creditToReturn = reg.getCourse().getCredit();

        Course course = courseRepository.findByIdForUpdate(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));
        course.setCurrentEnrollment(course.getCurrentEnrollment() - 1);
        courseRepository.save(course);

        student.setCurrentCredits(student.getCurrentCredits() - creditToReturn);
        studentRepository.save(student);

        registrationRepository.delete(reg);
    }

    /** [Student] 내 요청 대기 순번 목록 (강의별) */
    public List<QueuePosition> getMyQueuePositions(Long studentUserId) {
        return enrollQueueService.getMyQueuePositions(studentUserId);
    }

    /** [Student] 대기열 포기 (요청 큐에서 제거) */
    public void leaveQueue(Long studentUserId, Long courseId) {
        enrollQueueService.removeFromQueue(courseId, studentUserId);
    }

    /** [Student] 내 수강 내역 조회 */
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(Long studentUserId) {
        Student student = studentRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다."));
        return registrationRepository.findByStudentIdOrderByCreatedAtDesc(student.getId()).stream()
                .map(this::toEnrollmentResponse)
                .collect(Collectors.toList());
    }

    /** [Professor] 강의별 수강생 명단 */
    @Transactional(readOnly = true)
    public List<EnrolledStudentResponse> getStudentsByCourse(Long professorUserId, Long courseId) {
        Professor professor = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new IllegalArgumentException("교수 정보를 찾을 수 없습니다."));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));
        if (!course.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException("본인 강의의 수강생만 조회할 수 있습니다.");
        }
        return registrationRepository.findByCourseId(courseId).stream()
                .map(reg -> EnrolledStudentResponse.builder()
                        .registrationId(reg.getId())
                        .studentId(reg.getStudent().getId())
                        .studentNumber(reg.getStudent().getStudentNumber())
                        .studentName(reg.getStudent().getUser().getName())
                        .grade(reg.getStudent().getGrade())
                        .enrolledAt(reg.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    private EnrollmentResponse toEnrollmentResponse(Registration reg) {
        Course c = reg.getCourse();
        return EnrollmentResponse.builder()
                .registrationId(reg.getId())
                .courseId(c.getId())
                .courseCode(c.getCourseCode())
                .title(c.getTitle())
                .category(c.getCategory())
                .credit(c.getCredit())
                .schedule(c.getSchedule())
                .professorName(c.getProfessor().getUser().getName())
                .enrolledAt(reg.getCreatedAt())
                .build();
    }
}
