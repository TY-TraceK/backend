package com.tracek.domain.location.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracek.domain.location.application.client.TourImageClient;
import com.tracek.domain.location.application.dto.TourImageResult;
import com.tracek.domain.location.infrastructure.TourApiResponseCache;
import com.tracek.domain.location.infrastructure.config.TourApiProperties;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 한국관광공사 TourAPI4.0 detailImage2(이미지정보조회) 연동. 실시간 API 호출을 원칙으로 하며, 호출 실패 시 호출부(LocationFacade)에서
 * DB로 폴백한다.
 */
@Slf4j
@Component
public class TourApiImageClient implements TourImageClient {

    private static final String CACHE_KEY_PREFIX = "tourapi:image:";
    private static final Duration CACHE_TTL = Duration.ofHours(24);
    // 이미지가 없는 관광지는 나중에 등록될 수 있어 짧게 캐싱 (한도 절약 + 재확인 주기 단축)
    private static final Duration EMPTY_RESULT_TTL = Duration.ofHours(1);

    private final RestClient tourApiRestClient;
    private final TourApiProperties properties;
    private final ObjectMapper objectMapper;
    private final TourApiResponseCache cache;

    public TourApiImageClient(
            @Qualifier("tourApiRestClient") RestClient tourApiRestClient,
            TourApiProperties properties,
            ObjectMapper objectMapper,
            TourApiResponseCache cache) {
        this.tourApiRestClient = tourApiRestClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.cache = cache;
    }

    @Override
    public List<TourImageResult> getImages(Long externalContentId) {
        String key = CACHE_KEY_PREFIX + externalContentId;

        // Redis 캐시 조회 (Redis 장애 시에도 empty로 내려와 API 호출로 진행)
        Optional<String> cached = cache.get(key);

        // cache miss -> TourAPI 호출
        String rawBody = cached.orElseGet(() -> fetchFromTourApi(externalContentId));
        try {
            List<TourImageResult> results = parseImages(rawBody);
            // API로 새로 받아온 성공 응답만 원본 그대로 저장 (실패 응답은 parseImages에서 예외 -> 저장 안 됨)
            if (cached.isEmpty()) {
                Duration ttl = results.isEmpty() ? EMPTY_RESULT_TTL : CACHE_TTL;
                cache.set(key, rawBody, ttl);
            }
            return results;
        } catch (Exception e) {
            throw new IllegalStateException("TourAPI 이미지 응답 파싱 실패: " + e.getMessage(), e);
        }
    }

    private String fetchFromTourApi(Long externalContentId) {
        return tourApiRestClient
                .get()
                .uri(
                        uriBuilder ->
                                uriBuilder
                                        .path("/detailImage2")
                                        .queryParam("serviceKey", properties.serviceKey())
                                        .queryParam("contentId", externalContentId)
                                        .queryParam("imageYN", "Y")
                                        .queryParam("numOfRows", 30)
                                        .queryParam("pageNo", 1)
                                        .queryParam("MobileOS", properties.mobileOs())
                                        .queryParam("MobileApp", properties.mobileApp())
                                        .queryParam("_type", "json")
                                        .build())
                .retrieve()
                .body(String.class);
    }

    private List<TourImageResult> parseImages(String rawBody) throws Exception {

        JsonNode root = objectMapper.readTree(rawBody).path("response");
        String resultCode = root.path("header").path("resultCode").asText();
        if (!"0000".equals(resultCode)) {
            throw new IllegalStateException(
                    "TourAPI resultCode="
                            + resultCode
                            + ", msg="
                            + root.path("header").path("resultMsg").asText());
        }

        // items가 결과 없을 때 빈 문자열("")로 내려오는 공공데이터포털 특성 방어
        JsonNode itemNode = root.path("body").path("items").path("item");
        if (itemNode.isMissingNode() || !itemNode.isContainerNode()) {
            return List.of();
        }

        List<JsonNode> items = itemNode.isArray() ? toList(itemNode) : List.of(itemNode);
        List<TourImageResult> results = new ArrayList<>();
        for (JsonNode item : items) {
            String originImageUrl = item.path("originimgurl").asText(null);
            if (originImageUrl == null || originImageUrl.isBlank()) {
                continue;
            }
            results.add(
                    TourImageResult.of(originImageUrl, item.path("smallimageurl").asText(null)));
        }
        return results;
    }

    private List<JsonNode> toList(JsonNode arrayNode) {
        List<JsonNode> list = new ArrayList<>();
        arrayNode.forEach(list::add);
        return list;
    }
}
