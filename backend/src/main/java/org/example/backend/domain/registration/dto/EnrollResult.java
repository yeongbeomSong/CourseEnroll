package org.example.backend.domain.registration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 수강신청 결과: 즉시 신청 성공 또는 대기열 등록.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollResult {

    /** 즉시 수강신청 성공 시 내역 */
    private EnrollmentResponse enrollment;

    /** 대기열 등록 여부 */
    private boolean inWaitlist;

    /** 대기 순번 (1부터, 대기열일 때만) */
    private Long waitingPosition;

    public static EnrollResult enrolled(EnrollmentResponse enrollment) {
        return EnrollResult.builder()
                .enrollment(enrollment)
                .inWaitlist(false)
                .build();
    }

    public static EnrollResult waitlist(long position) {
        return EnrollResult.builder()
                .inWaitlist(true)
                .waitingPosition(position)
                .build();
    }
}
