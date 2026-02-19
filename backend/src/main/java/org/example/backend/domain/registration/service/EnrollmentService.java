package org.example.backend.domain.registration.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.course.entity.Course;
import org.example.backend.domain.course.repository.CourseRepository;
import org.example.backend.domain.registration.dto.EnrollResult;
import org.example.backend.domain.registration.dto.EnrolledStudentResponse;
import org.example.backend.domain.registration.dto.EnrollmentResponse;
import org.example.backend.domain.registration.entity.Registration;
import org.example.backend.domain.registration.repository.RegistrationRepository;
import org.example.backend.domain.registration.service.WaitingQueueService.WaitingPosition;
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
 * 수강신청·취소·내역·강의별 수강생 명단. 동시성 제어 포함.
 */
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final RegistrationRepository registrationRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final WaitingQueueService waitingQueueService;

    /** [Student] 수강신청 실행. 정원 마감 시 대기열 등록. */
    @Transactional
    public EnrollResult enroll(Long studentUserId, Long courseId) {
        Student student = studentRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다."));

        if (registrationRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new IllegalArgumentException("이미 수강신청한 강의입니다.");
        }

        Course course = courseRepository.findByIdForUpdate(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        if (!course.getTargetGrade().equals(student.getGrade())) {
            throw new IllegalArgumentException("대상 학년이 아닙니다.");
        }
        int afterCredits = student.getCurrentCredits() + course.getCredit();
        if (afterCredits > student.getMaxCredits()) {
            throw new IllegalArgumentException("신청 가능한 최대 학점을 초과합니다.");
        }

        if (course.getCurrentEnrollment() >= course.getCapacity()) {
            long position = waitingQueueService.addToQueue(courseId, studentUserId);
            return EnrollResult.waitlist(position);
        }

        Registration reg = Registration.builder()
                .student(student)
                .course(course)
                .createdAt(LocalDateTime.now())
                .build();
        reg = registrationRepository.save(reg);

        course.setCurrentEnrollment(course.getCurrentEnrollment() + 1);
        courseRepository.save(course);

        student.setCurrentCredits(student.getCurrentCredits() + course.getCredit());
        studentRepository.save(student);

        return EnrollResult.enrolled(toEnrollmentResponse(reg));
    }

    /** [Student] 수강신청 취소. 대기열 1명 자동 배정 시도. */
    @Transactional
    public void cancel(Long studentUserId, Long courseId) {
        Student student = studentRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다."));
        Registration reg = registrationRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new IllegalArgumentException("수강신청 내역을 찾을 수 없습니다."));
        int creditToReturn = reg.getCourse().getCredit();

        Course course = courseRepository.findByIdForUpdate(courseId).orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));
        course.setCurrentEnrollment(course.getCurrentEnrollment() - 1);
        courseRepository.save(course);

        student.setCurrentCredits(student.getCurrentCredits() - creditToReturn);
        studentRepository.save(student);

        registrationRepository.delete(reg);

        Long nextUserId = waitingQueueService.popNext(courseId);
        if (nextUserId != null) {
            try {
                enroll(nextUserId, courseId);
            } catch (Exception ignored) {
            }
        }
    }

    /** [Student] 내 대기열 순번 목록 */
    public List<WaitingPosition> getMyWaitingPositions(Long studentUserId) {
        return waitingQueueService.getMyWaitingPositions(studentUserId);
    }

    /** [Student] 대기열 포기 */
    public void leaveWaitingQueue(Long studentUserId, Long courseId) {
        waitingQueueService.removeFromQueue(courseId, studentUserId);
    }

    /** [Student] 내가 신청한 수강 내역 조회 (시간표 확인용) */
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(Long studentUserId) {
        Student student = studentRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다."));
        return registrationRepository.findByStudentIdOrderByCreatedAtDesc(student.getId()).stream()
                .map(this::toEnrollmentResponse)
                .collect(Collectors.toList());
    }

    /** [Professor] 특정 강의를 신청한 학생 명단 조회 */
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
