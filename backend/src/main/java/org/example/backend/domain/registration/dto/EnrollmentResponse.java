package org.example.backend.domain.registration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.course.entity.Category;

import java.time.LocalDateTime;

/**
 * 수강신청 내역 응답 DTO (내 수강 목록·시간표용).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private Long registrationId;
    private Long courseId;
    private String courseCode;
    private String title;
    private Category category;
    private Integer credit;
    private String schedule;
    private String professorName;
    private LocalDateTime enrolledAt;
}
