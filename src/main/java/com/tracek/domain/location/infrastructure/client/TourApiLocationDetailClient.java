package com.tracek.domain.location.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracek.domain.location.application.client.TourLocationDetailClient;
import com.tracek.domain.location.application.dto.TourLocationDetailResult;
import com.tracek.domain.location.infrastructure.config.TourApiProperties;
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

    private final RestClient tourApiRestClient;
    private final TourApiProperties properties;
    private final ObjectMapper objectMapper;

    public TourApiLocationDetailClient(
            @Qualifier("tourApiRestClient") RestClient tourApiRestClient,
            TourApiProperties properties,
            ObjectMapper objectMapper) {
        this.tourApiRestClient = tourApiRestClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public TourLocationDetailResult getDetail(Long externalContentId) {
        String rawBody =
                tourApiRestClient
                        .get()
                        .uri(
                                uriBuilder ->
                                        uriBuilder
                                                .path("/detailCommon2")
                                                .queryParam("serviceKey", properties.serviceKey())
                                                .queryParam("contentId", externalContentId)
                                                .queryParam("defaultYN", "Y")
                                                .queryParam("overviewYN", "Y")
                                                .queryParam("firstImageYN", "N")
                                                .queryParam("areacodeYN", "N")
                                                .queryParam("catcodeYN", "N")
                                                .queryParam("addrinfoYN", "N")
                                                .queryParam("mapinfoYN", "N")
                                                .queryParam("transGuideYN", "N")
                                                .queryParam("MobileOS", properties.mobileOs())
                                                .queryParam("MobileApp", properties.mobileApp())
                                                .queryParam("_type", "json")
                                                .build())
                        .retrieve()
                        .body(String.class);

        try {
            return parseDetail(rawBody);
        } catch (Exception e) {
            throw new IllegalStateException("TourAPI 상세 정보 응답 파싱 실패: " + e.getMessage(), e);
        }
    }

    private TourLocationDetailResult parseDetail(String rawBody) throws Exception {
        JsonNode root = objectMapper.readTree(rawBody).path("response");
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
