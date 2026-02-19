package org.example.backend.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 응답 DTO (JWT 토큰 및 기본 정보).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /** 발급된 액세스 토큰 */
    private String accessToken;

    /** 토큰 타입 (Bearer) */
    private String tokenType;

    /** 사용자 ID */
    private Long userId;

    /** 로그인 ID */
    private String username;

    /** 사용자 이름 */
    private String name;

    /** 권한 */
    private String role;
}
