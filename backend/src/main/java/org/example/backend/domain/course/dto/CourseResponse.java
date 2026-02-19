package org.example.backend.domain.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.course.entity.Category;

/**
 * 강의 목록·상세 응답 DTO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private Long id;
    private Long professorId;
    private String professorName;
    private Long departmentId;
    private String departmentName;
    private String courseCode;
    private String title;
    private Category category;
    private Integer credit;
    private Integer capacity;
    private Integer currentEnrollment;
    private Integer targetGrade;
    private String schedule;
}
