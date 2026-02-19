package org.example.backend.domain.registration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 강의별 수강생 명단 응답 DTO (교수용).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrolledStudentResponse {

    private Long registrationId;
    private Long studentId;
    private String studentNumber;
    private String studentName;
    private Integer grade;
    private LocalDateTime enrolledAt;
}
