package org.example.backend.domain.registration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 수강신청 상태 (폴링용).
 * - enrolled: 이미 수강 신청됨
 * - pending: 요청 대기열에서 대기 중 (position 있음)
 * - success: 방금 처리되어 신청 성공
 * - full: 정원 마감으로 실패
 * - error: 기타 실패
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollStatusResponse {

    private String status;  // enrolled, pending, success, full, error
    private Long position;   // 대기 순번 (pending일 때)
}
