package org.example.backend.domain.registration.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.registration.dto.EnrollRequest;
import org.example.backend.domain.registration.dto.EnrollResult;
import org.example.backend.domain.registration.dto.EnrollmentResponse;
import org.example.backend.domain.registration.service.EnrollmentService;
import org.example.backend.domain.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * [Student] 수강신청·취소·내 수강 내역·대기열 조회.
 */
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /** 수강신청 실행. 정원 마감 시 대기열 등록. Body: { "courseId": 101 } */
    @PostMapping
    public ResponseEntity<EnrollResult> enroll(@AuthenticationPrincipal User user,
                                               @Valid @RequestBody EnrollRequest request) {
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(enrollmentService.enroll(user.getId(), request.getCourseId()));
    }

    /** 내 대기열 순번 목록. { "positions": [ { "courseId": 1, "position": 2 }, ... ] } */
    @GetMapping("/waiting")
    public ResponseEntity<Map<String, List<Map<String, Long>>>> getMyWaitingPositions(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(401).build();
        List<Map<String, Long>> positions = enrollmentService.getMyWaitingPositions(user.getId()).stream()
                .map(p -> Map.<String, Long>of("courseId", p.courseId(), "position", p.position()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("positions", positions));
    }

    /** 대기열 포기 */
    @DeleteMapping("/waiting/{courseId}")
    public ResponseEntity<Void> leaveWaitingQueue(@AuthenticationPrincipal User user, @PathVariable Long courseId) {
        if (user == null) return ResponseEntity.status(401).build();
        enrollmentService.leaveWaitingQueue(user.getId(), courseId);
        return ResponseEntity.noContent().build();
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
