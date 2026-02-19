package org.example.backend.domain.registration.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.registration.dto.EnrollRequest;
import org.example.backend.domain.registration.dto.EnrollmentResponse;
import org.example.backend.domain.registration.service.EnrollmentService;
import org.example.backend.domain.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [Student] 수강신청·취소·내 수강 내역 조회.
 */
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /** 수강신청 실행. Body: { "courseId": 101 } */
    @PostMapping
    public ResponseEntity<EnrollmentResponse> enroll(@AuthenticationPrincipal User user,
                                                      @Valid @RequestBody EnrollRequest request) {
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(enrollmentService.enroll(user.getId(), request.getCourseId()));
    }

    /** 수강신청 취소 */
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> cancel(@AuthenticationPrincipal User user, @PathVariable Long courseId) {
        if (user == null) return ResponseEntity.status(401).build();
        enrollmentService.cancel(user.getId(), courseId);
        return ResponseEntity.noContent().build();
    }

    /** 내가 신청한 수강 내역 조회 (시간표 확인용) */
    @GetMapping("/me")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(user.getId()));
    }
}
