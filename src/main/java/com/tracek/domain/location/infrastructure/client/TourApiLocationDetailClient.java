package com.tracek.domain.location.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracek.domain.location.application.client.TourLocationDetailClient;
import com.tracek.domain.location.application.dto.TourLocationDetailResult;
import com.tracek.domain.location.infrastructure.TourApiResponseCache;
import com.tracek.domain.location.infrastructure.config.TourApiProperties;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 한국관광공사 TourAPI4.0 detailCommon2(공통정보조회) 연동. overview/tel만 실시간으로 받아오고, address/geoLocation은 방문 인증
 * 로직이 의존하는 값이라 API로 덮어쓰지 않는다.
 */
@Slf4j
@Component
public class TourApiLocationDetailClient implements TourLocationDetailClient {

    private static final String CACHE_TYPE = "detail";
    private static final String CACHE_KEY_PREFIX = "tourapi:detail:";
    // 개요/전화번호는 자주 바뀌지 않아 이미지와 같은 주기로 캐싱 (결과 없음은 parseDetail에서 예외 -> 저장 안 됨)
    private static final Duration CACHE_TTL = Duration.ofHours(24);

    private final RestClient tourApiRestClient;
    private final TourApiProperties properties;
    private final ObjectMapper objectMapper;
    private final TourApiResponseCache cache;
    private final MeterRegistry meterRegistry;

    public TourApiLocationDetailClient(
            @Qualifier("tourApiRestClient") RestClient tourApiRestClient,
            TourApiProperties properties,
            ObjectMapper objectMapper,
            TourApiResponseCache cache,
            MeterRegistry meterRegistry) {
        this.tourApiRestClient = tourApiRestClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.cache = cache;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public TourLocationDetailResult getDetail(Long externalContentId) {
        String key = CACHE_KEY_PREFIX + externalContentId;

        // Redis 캐시 조회 (Redis 장애 시에도 empty로 내려와 API 호출로 진행)
        Optional<String> cached = cache.get(CACHE_TYPE, key);

        // cache miss -> TourAPI 호출
        String rawBody = cached.orElseGet(() -> fetchFromTourApi(externalContentId));
        try {
            TourLocationDetailResult result = parseDetail(rawBody);
            // API로 새로 받아온 성공 응답만 원본 그대로 저장 (실패/결과 없음은 parseDetail에서 예외 -> 저장 안 됨)
            if (cached.isEmpty()) {
                cache.set(CACHE_TYPE, key, rawBody, CACHE_TTL);
            }
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("TourAPI 상세 정보 응답 파싱 실패: " + e.getMessage(), e);
        }
    }

    private String fetchFromTourApi(Long externalContentId) {
        // 실제 TourAPI 호출 수 (1일 호출 한도 대비 캐시 효과 측정용)
        meterRegistry.counter("tourapi.calls", "type", CACHE_TYPE).increment();
        return tourApiRestClient
                .get()
                .uri(
                        uriBuilder ->
                                uriBuilder
                                        .path("/detailCommon2")
                                        .queryParam("serviceKey", properties.serviceKey())
                                        .queryParam("contentId", externalContentId)
                                        // KorService2 detailCommon2는 defaultYN/overviewYN 등 *YN 옵션을
                                        // 받지 않는다 (보내면 resultCode=10
                                        // INVALID_REQUEST_PARAMETER_ERROR).
                                        // 옵션 없이도 overview/tel을 포함한 공통정보 전체가 내려온다.
                                        .queryParam("MobileOS", properties.mobileOs())
                                        .queryParam("MobileApp", properties.mobileApp())
                                        .queryParam("_type", "json")
                                        .build())
                .retrieve()
                .body(String.class);
    }

    private TourLocationDetailResult parseDetail(String rawBody) throws Exception {
        JsonNode body = objectMapper.readTree(rawBody);
        // 요청 파라미터 오류 등은 response.header가 아니라 최상위에 resultCode/resultMsg로 내려온다
        if (body.path("response").isMissingNode() && body.has("resultCode")) {
            throw new IllegalStateException(
                    "TourAPI resultCode="
                            + body.path("resultCode").asText()
                            + ", msg="
                            + body.path("resultMsg").asText());
        }

        JsonNode root = body.path("response");
        String resultCode = root.path("header").path("resultCode").asText();
        if (!"0000".equals(resultCode)) {
            throw new IllegalStateException(
                    "TourAPI resultCode="
                            + resultCode
                            + ", msg="
                            + root.path("header").path("resultMsg").asText());
        }

        // 결과 없을 때 items가 빈 문자열("")로 내려오는 공공데이터포털 특성 방어
        JsonNode itemNode = root.path("body").path("items").path("item");
        if (itemNode.isArray() && itemNode.size() > 0) {
            itemNode = itemNode.get(0);
        }
        if (itemNode.isMissingNode() || !itemNode.isObject()) {
            throw new IllegalStateException("TourAPI 응답에 상세 정보(item)가 없습니다.");
        }

        return TourLocationDetailResult.of(
                itemNode.path("overview").asText(null), itemNode.path("tel").asText(null));
    }
}
