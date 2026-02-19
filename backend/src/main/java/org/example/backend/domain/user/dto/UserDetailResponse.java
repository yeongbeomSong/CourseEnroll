package org.example.backend.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.user.entity.Role;

/**
 * 관리자용 사용자 상세 응답 DTO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {

    private Long id;
    private String username;
    private String name;
    private Role role;

    private Long departmentId;
    private String departmentName;
    private String departmentCode;

    private String studentNumber;
    private Integer grade;
    private Integer maxCredits;
    private Integer currentCredits;

    private String professorCode;
    private String position;
}
