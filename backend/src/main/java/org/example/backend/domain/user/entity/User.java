package org.example.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 엔티티.
 * 로그인 및 권한 인증을 위한 공통 계정 정보를 담는다.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** PK */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 로그인 ID (학번 또는 사번, Unique) */
    @Column(nullable = false, unique = true)
    private String username;

    /** 암호화된 비밀번호 */
    @Column(nullable = false)
    private String password;

    /** 본명 */
    @Column(nullable = false)
    private String name;

    /** 권한 (ROLE_STUDENT, ROLE_PROFESSOR, ROLE_ADMIN) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
