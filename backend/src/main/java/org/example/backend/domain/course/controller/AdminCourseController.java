package org.example.backend.domain.course.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.course.dto.CourseResponse;
import org.example.backend.domain.course.service.CourseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * [Admin] 전체 강의 모니터링.
 */
@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(
                courseService.findAll(null, null, null, PageRequest.of(0, 5000)).getContent()
        );
    }
}
