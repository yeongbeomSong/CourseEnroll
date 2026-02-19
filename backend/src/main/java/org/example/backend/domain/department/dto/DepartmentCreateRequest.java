package org.example.backend.domain.department.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 학과 신설 요청 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
public class DepartmentCreateRequest {

    @NotBlank(message = "학과명은 필수입니다.")
    private String name;

    @NotBlank(message = "학과 코드는 필수입니다.")
    private String deptCode;

    @NotBlank(message = "단과대학명은 필수입니다.")
    private String college;
}
