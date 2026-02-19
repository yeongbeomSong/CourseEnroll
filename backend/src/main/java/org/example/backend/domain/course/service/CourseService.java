package org.example.backend.domain.course.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.course.dto.CourseCreateRequest;
import org.example.backend.domain.course.dto.CourseResponse;
import org.example.backend.domain.course.dto.CourseUpdateRequest;
import org.example.backend.domain.course.entity.Course;
import org.example.backend.domain.course.entity.Category;
import org.example.backend.domain.course.repository.CourseRepository;
import org.example.backend.domain.department.entity.Department;
import org.example.backend.domain.department.repository.DepartmentRepository;
import org.example.backend.domain.professor.entity.Professor;
import org.example.backend.domain.professor.repository.ProfessorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 강의 조회·등록·수정·삭제 서비스.
 */
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final ProfessorRepository professorRepository;

    /** 전체 강의 목록 (필터: 학과, 학년, 이수구분) */
    @Transactional(readOnly = true)
    public Page<CourseResponse> findAll(Long departmentId, Integer targetGrade, Category category, Pageable pageable) {
        Specification<Course> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (departmentId != null) {
                predicates.add(cb.equal(root.get("department").get("id"), departmentId));
            }
            if (targetGrade != null) {
                predicates.add(cb.equal(root.get("targetGrade"), targetGrade));
            }
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return courseRepository.findAll(spec, pageable).map(this::toResponse);
    }

    /** 특정 강의 상세 조회 */
    @Transactional(readOnly = true)
    public CourseResponse findById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));
        return toResponse(course);
    }

    /** [Professor] 신규 강의 등록 */
    @Transactional
    public CourseResponse createByProfessor(Long professorUserId, CourseCreateRequest request) {
        Professor professor = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new IllegalArgumentException("교수 정보를 찾을 수 없습니다."));
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("학과를 찾을 수 없습니다."));

        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new IllegalArgumentException("이미 사용 중인 과목 코드입니다.");
        }

        Course course = Course.builder()
                .professor(professor)
                .department(department)
                .courseCode(request.getCourseCode())
                .title(request.getTitle())
                .category(request.getCategory())
                .credit(request.getCredit())
                .capacity(request.getCapacity())
                .currentEnrollment(0)
                .targetGrade(request.getTargetGrade())
                .schedule(request.getSchedule())
                .build();
        course = courseRepository.save(course);
        return toResponse(course);
    }

    /** [Professor] 내 강의 정보 수정 */
    @Transactional
    public CourseResponse updateByProfessor(Long professorUserId, Long courseId, CourseUpdateRequest request) {
        Professor professor = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new IllegalArgumentException("교수 정보를 찾을 수 없습니다."));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));
        if (!course.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException("본인 강의만 수정할 수 있습니다.");
        }

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("학과를 찾을 수 없습니다."));
            course.setDepartment(dept);
        }
        if (request.getCourseCode() != null && !request.getCourseCode().isBlank()) {
            if (courseRepository.existsByCourseCode(request.getCourseCode()) && !request.getCourseCode().equals(course.getCourseCode())) {
                throw new IllegalArgumentException("이미 사용 중인 과목 코드입니다.");
            }
            course.setCourseCode(request.getCourseCode());
        }
        if (request.getTitle() != null && !request.getTitle().isBlank()) course.setTitle(request.getTitle());
        if (request.getCategory() != null) course.setCategory(request.getCategory());
        if (request.getCredit() != null) course.setCredit(request.getCredit());
        if (request.getCapacity() != null) course.setCapacity(request.getCapacity());
        if (request.getTargetGrade() != null) course.setTargetGrade(request.getTargetGrade());
        if (request.getSchedule() != null && !request.getSchedule().isBlank()) course.setSchedule(request.getSchedule());

        return toResponse(courseRepository.save(course));
    }

    /** [Professor] 내 강의 목록 조회 */
    @Transactional(readOnly = true)
    public List<CourseResponse> findCoursesByProfessorUserId(Long professorUserId) {
        Professor professor = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new IllegalArgumentException("교수 정보를 찾을 수 없습니다."));
        return courseRepository.findByProfessorId(professor.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    /** [Professor] 강의 폐강/삭제 */
    @Transactional
    public void deleteByProfessor(Long professorUserId, Long courseId) {
        Professor professor = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new IllegalArgumentException("교수 정보를 찾을 수 없습니다."));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));
        if (!course.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException("본인 강의만 삭제할 수 있습니다.");
        }
        courseRepository.delete(course);
    }

    private CourseResponse toResponse(Course c) {
        return CourseResponse.builder()
                .id(c.getId())
                .professorId(c.getProfessor().getId())
                .professorName(c.getProfessor().getUser().getName())
                .departmentId(c.getDepartment().getId())
                .departmentName(c.getDepartment().getName())
                .courseCode(c.getCourseCode())
                .title(c.getTitle())
                .category(c.getCategory())
                .credit(c.getCredit())
                .capacity(c.getCapacity())
                .currentEnrollment(c.getCurrentEnrollment())
                .targetGrade(c.getTargetGrade())
                .schedule(c.getSchedule())
                .build();
    }
}
