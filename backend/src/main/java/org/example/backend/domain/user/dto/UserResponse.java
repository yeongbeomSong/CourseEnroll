package org.example.backend.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.user.entity.Role;

/**
 * 사용자 정보 응답 DTO (마이페이지·목록·상세 공통용).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String name;
    private Role role;

    /** 학생일 때 학번 */
    private String studentNumber;
    /** 학생일 때 학년 */
    private Integer grade;
    /** 교수일 때 사번 */
    private String professorCode;
    /** 교수일 때 직위 */
    private String position;
    /** 학과명 */
    private String departmentName;
    /** 학과 코드 */
    private String departmentCode;
}
