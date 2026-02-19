package org.example.backend.domain.user.entity;

/**
 * 사용자 권한 구분.
 * 로그인 및 인증 시 권한에 따른 접근 제어에 사용된다.
 */
public enum Role {
    /** 학생 */
    ROLE_STUDENT,
    /** 교수 */
    ROLE_PROFESSOR,
    /** 관리자 */
    ROLE_ADMIN
}
