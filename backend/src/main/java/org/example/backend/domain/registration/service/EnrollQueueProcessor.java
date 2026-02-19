package org.example.backend.domain.registration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 선착순 수강신청 큐를 주기적으로 처리.
 * 강의별로 한 명씩 꺼내 실제 수강신청 수행.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollQueueProcessor {

    private final EnrollQueueService enrollQueueService;
    private final EnrollmentService enrollmentService;

    @Scheduled(fixedDelay = 500)
    public void processQueues() {
        Set<String> courseIds = enrollQueueService.getActiveCourseIds();
        for (String courseIdStr : courseIds) {
            try {
                Long courseId = Long.parseLong(courseIdStr);
                enrollmentService.processOneFromQueue(courseId);
            } catch (Exception e) {
                log.debug("Enroll queue process skip: {}", e.getMessage());
            }
        }
    }
}
