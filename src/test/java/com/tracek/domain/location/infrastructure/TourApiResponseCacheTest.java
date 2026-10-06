package com.tracek.domain.location.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class TourApiResponseCacheTest {

    private static final String TYPE = "image";
    private static final String KEY = "tourapi:image:1";
    private static final Duration TTL = Duration.ofHours(24);

    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;

    private SimpleMeterRegistry meterRegistry;
    private TourApiResponseCache cache;

    @BeforeEach
    void setUp() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        meterRegistry = new SimpleMeterRegistry();
        cache = new TourApiResponseCache(redisTemplate, meterRegistry);
    }

    @Test
    @DisplayName("캐시에 값이 있으면 그대로 반환하고 hit으로 집계한다")
    void get_hit_returnsValueAndCountsHit() {
        given(valueOperations.get(KEY)).willReturn("{\"response\":{}}");

        Optional<String> result = cache.get(TYPE, KEY);

        assertThat(result).contains("{\"response\":{}}");
        assertThat(count("hit")).isEqualTo(1.0);
        assertThat(count("miss")).isZero();
    }

    @Test
    @DisplayName("캐시에 값이 없으면 empty를 반환하고 miss로 집계한다")
    void get_miss_returnsEmptyAndCountsMiss() {
        given(valueOperations.get(KEY)).willReturn(null);

        assertThat(cache.get(TYPE, KEY)).isEmpty();
        assertThat(count("miss")).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Redis 장애로 조회에 실패해도 예외 없이 empty를 반환하고 error로 집계한다 (API 호출로 진행되도록)")
    void get_redisFailure_returnsEmptyAndCountsError() {
        given(valueOperations.get(KEY))
                .willThrow(new RedisConnectionFailureException("connection refused"));

        assertThat(cache.get(TYPE, KEY)).isEmpty();
        assertThat(count("error")).isEqualTo(1.0);
    }

    @Test
    @DisplayName("저장 시 기본 TTL에 최대 10%의 지터를 더해 저장한다")
    void set_savesWithJitteredTtl() {
        cache.set(TYPE, KEY, "raw", TTL);

        ArgumentCaptor<Duration> ttlCaptor = ArgumentCaptor.forClass(Duration.class);
        verify(valueOperations).set(eq(KEY), eq("raw"), ttlCaptor.capture());
        assertThat(ttlCaptor.getValue()).isBetween(TTL, TTL.plusSeconds(TTL.toSeconds() / 10));
    }

    @Test
    @DisplayName("Redis 장애로 저장에 실패해도 예외를 던지지 않고 set_error로 집계한다 (요청 처리가 실패하면 안 됨)")
    void set_redisFailure_doesNotThrowAndCountsSetError() {
        willThrow(new RedisConnectionFailureException("connection refused"))
                .given(valueOperations)
                .set(eq(KEY), eq("raw"), any(Duration.class));

        assertThatCode(() -> cache.set(TYPE, KEY, "raw", TTL)).doesNotThrowAnyException();
        assertThat(count("set_error")).isEqualTo(1.0);
    }

    private double count(String result) {
        return meterRegistry.counter("tourapi.cache", "type", TYPE, "result", result).count();
    }
}
