package org.example.backend.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.user.dto.UserDetailResponse;
import org.example.backend.domain.user.dto.UserResponse;
import org.example.backend.domain.user.entity.Role;
import org.example.backend.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 전용: 유저 목록/상세/삭제.
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    /**
     * 전체 유저 목록 조회 (필터: 학번/사번(keyword), 이름, 역할).
     */
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Role role,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersForAdmin(keyword, name, role, pageable));
    }

    /**
     * 특정 유저 상세 정보 조회.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserDetailForAdmin(id));
    }

    /**
     * 유저 삭제 (Admin 전용).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserByAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
