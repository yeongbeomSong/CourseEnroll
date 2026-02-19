package org.example.backend.domain.user.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.domain.user.entity.Role;

/**
 * 회원가입 요청 DTO.
 * 학생/교수 구분에 따라 일부 필드만 사용한다.
 */
@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {

    @NotBlank(message = "로그인 ID(학번/사번)는 필수입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    /** 학생(ROLE_STUDENT) 또는 교수(ROLE_PROFESSOR) */
    @NotNull(message = "역할은 필수입니다.")
    private Role role;

    /** 소속 학과 ID (학생/교수 공통) */
    @NotNull(message = "학과는 필수입니다.")
    private Long departmentId;

    // ---- 학생 전용 ----
    /** 학번 (role=ROLE_STUDENT일 때 필수, username과 동일해도 됨) */
    private String studentNumber;

    @Min(1)
    @Max(4)
    private Integer grade;

    @Min(1)
    @Max(24)
    private Integer maxCredits;

    // ---- 교수 전용 ----
    /** 사번 (role=ROLE_PROFESSOR일 때 필수) */
    private String professorCode;

    /** 직위 (정교수, 부교수 등) */
    private String position;
}
