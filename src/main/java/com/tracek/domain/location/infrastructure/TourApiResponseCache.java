package com.tracek.domain.location.infrastructure;

import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * TourAPI 원본 응답(JSON 문자열) 캐시. Redis 장애가 요청 실패로 번지지 않도록 조회/저장 실패는 예외 대신 로그와 메트릭으로만 남긴다.
 *
 * <p>메트릭: {@code tourapi.cache{type=image|detail, result=hit|miss|error|set_error}}
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TourApiResponseCache {

    private static final String METRIC_NAME = "tourapi.cache";
    // 같은 시점에 채워진 키들이 한꺼번에 만료되어 TourAPI로 요청이 몰리지 않도록 TTL에 최대 10%를 랜덤으로 더한다
    private static final int TTL_JITTER_PERCENT = 10;

    private final StringRedisTemplate redisTemplate;
    private final MeterRegistry meterRegistry;

    // 조회
    public Optional<String> get(String type, String key) {
        try {
            Optional<String> cached = Optional.ofNullable(redisTemplate.opsForValue().get(key));
            count(type, cached.isPresent() ? "hit" : "miss");
            return cached;
        } catch (DataAccessException e) {
            count(type, "error");
            log.warn("TourAPI 캐시 조회 실패. key = {}", key, e);
            return Optional.empty();
        }
    }

    // 저장
    public void set(String type, String key, String value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, withJitter(ttl));
        } catch (DataAccessException e) {
            count(type, "set_error");
            log.warn("TourAPI 캐시 저장 실패. key = {}", key, e);
        }
    }

    private Duration withJitter(Duration ttl) {
        long maxJitterSeconds = ttl.toSeconds() * TTL_JITTER_PERCENT / 100;
        return ttl.plusSeconds(ThreadLocalRandom.current().nextLong(maxJitterSeconds + 1));
    }

    private void count(String type, String result) {
        meterRegistry.counter(METRIC_NAME, "type", type, "result", result).increment();
    }
}
