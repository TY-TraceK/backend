package com.tracek.domain.location.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class TourApiResponseCacheTest {

    private static final String KEY = "tourapi:image:1";

    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;

    @InjectMocks private TourApiResponseCache cache;

    @BeforeEach
    void setUp() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Test
    @DisplayName("캐시에 값이 있으면 그대로 반환한다")
    void get_hit_returnsValue() {
        given(valueOperations.get(KEY)).willReturn("{\"response\":{}}");

        Optional<String> result = cache.get(KEY);

        assertThat(result).contains("{\"response\":{}}");
    }

    @Test
    @DisplayName("캐시에 값이 없으면 empty를 반환한다")
    void get_miss_returnsEmpty() {
        given(valueOperations.get(KEY)).willReturn(null);

        assertThat(cache.get(KEY)).isEmpty();
    }

    @Test
    @DisplayName("Redis 장애로 조회에 실패해도 예외를 던지지 않고 empty를 반환한다 (API 호출로 진행되도록)")
    void get_redisFailure_returnsEmpty() {
        given(valueOperations.get(KEY))
                .willThrow(new RedisConnectionFailureException("connection refused"));

        assertThat(cache.get(KEY)).isEmpty();
    }

    @Test
    @DisplayName("값을 TTL과 함께 저장한다")
    void set_savesWithTtl() {
        cache.set(KEY, "raw", Duration.ofHours(24));

        verify(valueOperations).set(KEY, "raw", Duration.ofHours(24));
    }

    @Test
    @DisplayName("Redis 장애로 저장에 실패해도 예외를 던지지 않는다 (요청 처리가 실패하면 안 됨)")
    void set_redisFailure_doesNotThrow() {
        willThrow(new RedisConnectionFailureException("connection refused"))
                .given(valueOperations)
                .set(KEY, "raw", Duration.ofHours(24));

        assertThatCode(() -> cache.set(KEY, "raw", Duration.ofHours(24)))
                .doesNotThrowAnyException();
    }
}
