package org.example.backend.domain.registration.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 수강신청 요청 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
public class EnrollRequest {

    @NotNull(message = "강의 ID는 필수입니다.")
    private Long courseId;
}
