package org.example.backend.domain.registration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Redis 기반 수강신청 대기열.
 * 강의별 FIFO 큐, 사용자별 대기 중인 강의 목록 관리.
 */
@Service
@RequiredArgsConstructor
public class WaitingQueueService {

    private static final String KEY_COURSE_QUEUE = "waiting:course:%d";
    private static final String KEY_USER_COURSES = "waiting:user:%s";

    private final StringRedisTemplate redisTemplate;

    /** 대기열에 추가. 이미 대기 중이면 순번만 반환. */
    public long addToQueue(Long courseId, Long userId) {
        String key = String.format(KEY_COURSE_QUEUE, courseId);
        String uid = String.valueOf(userId);
        Long pos = redisTemplate.opsForList().indexOf(key, uid);
        if (pos != null && pos >= 0) {
            return pos + 1;
        }
        redisTemplate.opsForList().rightPush(key, uid);
        redisTemplate.opsForSet().add(String.format(KEY_USER_COURSES, uid), String.valueOf(courseId));
        Long size = redisTemplate.opsForList().size(key);
        return size != null ? size : 0;
    }

    /** 대기 순번 (1부터). 없으면 0. */
    public long getPosition(Long courseId, Long userId) {
        String key = String.format(KEY_COURSE_QUEUE, courseId);
        String uid = String.valueOf(userId);
        Long index = redisTemplate.opsForList().indexOf(key, uid);
        if (index == null || index < 0) return 0;
        return index + 1;
    }

    /** 대기열에서 한 명 꺼내기. 없으면 null. */
    public Long popNext(Long courseId) {
        String key = String.format(KEY_COURSE_QUEUE, courseId);
        String uid = redisTemplate.opsForList().leftPop(key);
        if (uid == null) return null;
        redisTemplate.opsForSet().remove(String.format(KEY_USER_COURSES, uid), String.valueOf(courseId));
        return Long.parseLong(uid);
    }

    /** 대기열에서 사용자 제거 (포기 등) */
    public void removeFromQueue(Long courseId, Long userId) {
        String key = String.format(KEY_COURSE_QUEUE, courseId);
        String uid = String.valueOf(userId);
        redisTemplate.opsForList().remove(key, 1, uid);
        redisTemplate.opsForSet().remove(String.format(KEY_USER_COURSES, uid), String.valueOf(courseId));
    }

    /** 해당 강의 대기열에 있는지 */
    public boolean isInQueue(Long courseId, Long userId) {
        return getPosition(courseId, userId) > 0;
    }

    /** 내가 대기 중인 강의 ID 목록 */
    public List<Long> getMyWaitingCourseIds(Long userId) {
        Set<String> set = redisTemplate.opsForSet().members(String.format(KEY_USER_COURSES, String.valueOf(userId)));
        if (set == null) return List.of();
        return set.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    /** (강의 ID, 대기 순번) 목록 */
    public List<WaitingPosition> getMyWaitingPositions(Long userId) {
        List<Long> courseIds = getMyWaitingCourseIds(userId);
        List<WaitingPosition> result = new ArrayList<>();
        for (Long courseId : courseIds) {
            long pos = getPosition(courseId, userId);
            if (pos > 0) result.add(new WaitingPosition(courseId, pos));
        }
        return result;
    }

    public record WaitingPosition(long courseId, long position) {}
}
