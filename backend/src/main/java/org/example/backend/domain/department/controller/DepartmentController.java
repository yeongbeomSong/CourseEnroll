package org.example.backend.domain.department.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.department.dto.DepartmentResponse;
import org.example.backend.domain.department.service.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 학과 API: 전체 학과 목록 조회 (모든 사용자).
 */
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /** 전체 학과 목록 조회 */
    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAll() {
        return ResponseEntity.ok(departmentService.findAll());
    }
}
