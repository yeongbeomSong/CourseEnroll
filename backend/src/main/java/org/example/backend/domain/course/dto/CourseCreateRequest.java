package org.example.backend.domain.course.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.domain.course.entity.Category;

/**
 * 강의 신규 등록 요청 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
public class CourseCreateRequest {

    @NotNull(message = "개설 학과는 필수입니다.")
    private Long departmentId;

    @NotBlank(message = "과목 코드는 필수입니다.")
    private String courseCode;

    @NotBlank(message = "강의명은 필수입니다.")
    private String title;

    @NotNull(message = "이수 구분은 필수입니다.")
    private Category category;

    @NotNull
    @Min(1)
    @Max(3)
    private Integer credit;

    @NotNull
    @Min(1)
    private Integer capacity;

    @NotNull
    @Min(1)
    @Max(4)
    private Integer targetGrade;

    @NotBlank(message = "강의 시간은 필수입니다.")
    private String schedule;
}
