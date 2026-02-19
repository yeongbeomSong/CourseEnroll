package org.example.backend.domain.course.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.domain.course.entity.Category;

/**
 * 강의 정보 수정 요청 DTO. (null이면 변경 안 함)
 */
@Getter
@Setter
@NoArgsConstructor
public class CourseUpdateRequest {

    private Long departmentId;
    private String courseCode;
    private String title;
    private Category category;
    private Integer credit;
    private Integer capacity;
    private Integer targetGrade;
    private String schedule;
}
