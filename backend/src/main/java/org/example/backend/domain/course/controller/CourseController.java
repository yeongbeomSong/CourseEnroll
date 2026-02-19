package org.example.backend.domain.course.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.course.dto.CourseResponse;
import org.example.backend.domain.course.entity.Category;
import org.example.backend.domain.course.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 강의 조회 API: 목록·상세 (검색 필터: 학과, 학년, 이수구분).
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /** 전체 강의 목록 조회 (필터: departmentId, targetGrade, category) */
    @GetMapping
    public ResponseEntity<Page<CourseResponse>> getAll(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer targetGrade,
            @RequestParam(required = false) Category category,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(courseService.findAll(departmentId, targetGrade, category, pageable));
    }

    /** 특정 강의 상세 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.findById(id));
    }
}
