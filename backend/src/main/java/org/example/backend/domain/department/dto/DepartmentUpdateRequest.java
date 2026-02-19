package org.example.backend.domain.department.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 학과 정보 수정 요청 DTO. (null이면 변경 안 함)
 */
@Getter
@Setter
@NoArgsConstructor
public class DepartmentUpdateRequest {

    private String name;
    private String deptCode;
    private String college;
}
