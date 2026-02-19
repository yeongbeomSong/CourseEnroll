package org.example.backend.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * JWT 설정 값 (application.properties에서 주입).
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** 비밀 키 (토큰 서명/검증용) */
    private String secret = "default-secret-change-in-production-minimum-256-bits-for-hs256";

    /** 액세스 토큰 만료 시간 (밀리초) */
    private long accessTokenExpirationMs = 86400000; // 24시간
}
