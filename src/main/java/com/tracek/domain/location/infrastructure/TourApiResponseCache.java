package com.tracek.domain.location.infrastructure;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TourApiResponseCache {

    private final StringRedisTemplate redisTemplate;

    // 조회
    public Optional<String> get(String key) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(key));
        } catch (DataAccessException e) {
            log.warn("TourAPI 캐시 조회 실패. key = {}", key, e);
            return Optional.empty();
        }
    }

    // 저장
    public void set(String key, String value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl);
        } catch (DataAccessException e) {
            log.warn("TourAPI 캐시 저장 실패. key = {}", key, e);
        }
    }
}
