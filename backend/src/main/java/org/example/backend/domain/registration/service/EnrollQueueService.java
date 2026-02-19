package org.example.backend.domain.registration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 선착순 수강신청 요청 대기열 (Redis).
 * 신청 요청을 강의별 큐에 넣고, 스케줄러가 순서대로 처리한다.
 */
@Service
@RequiredArgsConstructor
public class EnrollQueueService {

    private static final String KEY_QUEUE = "enroll:queue:%d";
    private static final String KEY_ACTIVE = "enroll:active";
    private static final String KEY_RESULT = "enroll:result:%s:%s";
    private static final String KEY_USER_QUEUES = "enroll:user:%s";
    private static final Duration RESULT_TTL = Duration.ofSeconds(300);

    private final StringRedisTemplate redis;

    /** 요청을 큐에 넣음. 이미 있으면 기존 순번 반환. */
    public long enqueue(Long courseId, Long userId) {
        String qKey = String.format(KEY_QUEUE, courseId);
        String uid = String.valueOf(userId);
        Long idx = redis.opsForList().indexOf(qKey, uid);
        if (idx != null && idx >= 0) return idx + 1;

        redis.opsForList().rightPush(qKey, uid);
        redis.opsForSet().add(KEY_ACTIVE, String.valueOf(courseId));
        redis.opsForSet().add(String.format(KEY_USER_QUEUES, uid), String.valueOf(courseId));
        Long size = redis.opsForList().size(qKey);
        return size != null ? size : 0;
    }

    /** 큐에서 순번 (1부터). 없으면 0 */
    public long getPosition(Long courseId, Long userId) {
        String qKey = String.format(KEY_QUEUE, courseId);
        String uid = String.valueOf(userId);
        Long idx = redis.opsForList().indexOf(qKey, uid);
        if (idx == null || idx < 0) return 0;
        return idx + 1;
    }

    /** 큐에서 한 명 꺼냄. 없으면 null. */
    public Long popOne(Long courseId) {
        String qKey = String.format(KEY_QUEUE, courseId);
        String uid = redis.opsForList().leftPop(qKey);
        if (uid == null) return null;
        redis.opsForSet().remove(String.format(KEY_USER_QUEUES, uid), String.valueOf(courseId));
        Long size = redis.opsForList().size(qKey);
        if (size == null || size == 0) redis.opsForSet().remove(KEY_ACTIVE, String.valueOf(courseId));
        return Long.parseLong(uid);
    }

    /** 처리 결과 저장 (폴링 시 한 번 읽고 삭제) */
    public void setResult(Long userId, Long courseId, String status) {
        redis.opsForValue().set(String.format(KEY_RESULT, userId, courseId), status, RESULT_TTL);
    }

    /** 결과 조회 후 삭제 (한 번만 반환) */
    public String getAndClearResult(Long userId, Long courseId) {
        String key = String.format(KEY_RESULT, userId, courseId);
        String v = redis.opsForValue().get(key);
        if (v != null) redis.delete(key);
        return v;
    }

    /** 큐에서 사용자 제거 (대기열 포기) */
    public void removeFromQueue(Long courseId, Long userId) {
        String qKey = String.format(KEY_QUEUE, courseId);
        String uid = String.valueOf(userId);
        redis.opsForList().remove(qKey, 1, uid);
        redis.opsForSet().remove(String.format(KEY_USER_QUEUES, uid), String.valueOf(courseId));
        Long size = redis.opsForList().size(qKey);
        if (size == null || size == 0) redis.opsForSet().remove(KEY_ACTIVE, String.valueOf(courseId));
    }

    /** 큐에 대기 중인 강의 ID 목록 */
    public List<Long> getMyQueuedCourseIds(Long userId) {
        Set<String> set = redis.opsForSet().members(String.format(KEY_USER_QUEUES, String.valueOf(userId)));
        if (set == null) return List.of();
        return set.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    /** (강의 ID, 순번) 목록 */
    public List<QueuePosition> getMyQueuePositions(Long userId) {
        List<Long> courseIds = getMyQueuedCourseIds(userId);
        List<QueuePosition> result = new ArrayList<>();
        for (Long courseId : courseIds) {
            long pos = getPosition(courseId, userId);
            if (pos > 0) result.add(new QueuePosition(courseId, pos));
        }
        return result;
    }

    /** 처리할 큐가 있는 강의 ID 목록 (스케줄러용) */
    public Set<String> getActiveCourseIds() {
        Set<String> set = redis.opsForSet().members(KEY_ACTIVE);
        return set != null ? set : Set.of();
    }

    public record QueuePosition(long courseId, long position) {}
}
