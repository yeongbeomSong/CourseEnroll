package org.example.backend.domain.course.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.course.dto.CourseCreateRequest;
import org.example.backend.domain.course.dto.CourseResponse;
import org.example.backend.domain.course.dto.CourseUpdateRequest;
import org.example.backend.domain.course.service.CourseService;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.registration.dto.EnrolledStudentResponse;
import org.example.backend.domain.registration.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [Professor] 강의 등록·수정·삭제, 강의별 수강생 명단 조회.
 */
@RestController
@RequestMapping("/api/professors/courses")
@RequiredArgsConstructor
public class ProfessorCourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    /** 내 강의 목록 조회 */
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getMyCourses(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(courseService.findCoursesByProfessorUserId(user.getId()));
    }

    /** 신규 강의 등록 */
    @PostMapping
    public ResponseEntity<CourseResponse> create(@AuthenticationPrincipal User user,
                                                  @Valid @RequestBody CourseCreateRequest request) {
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(courseService.createByProfessor(user.getId(), request));
    }

    /** 내 강의 정보 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> update(@AuthenticationPrincipal User user,
                                                  @PathVariable Long id,
                                                  @RequestBody CourseUpdateRequest request) {
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(courseService.updateByProfessor(user.getId(), id, request));
    }

    /** 강의 폐강/삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        if (user == null) return ResponseEntity.status(401).build();
        courseService.deleteByProfessor(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    /** 특정 강의를 신청한 학생 명단 조회 */
    @GetMapping("/{id}/students")
    public ResponseEntity<List<EnrolledStudentResponse>> getEnrolledStudents(@AuthenticationPrincipal User user,
                                                                             @PathVariable Long id) {
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(enrollmentService.getStudentsByCourse(user.getId(), id));
    }
}
