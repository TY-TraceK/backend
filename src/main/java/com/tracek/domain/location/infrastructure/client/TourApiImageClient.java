package com.tracek.domain.location.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracek.domain.location.application.client.TourImageClient;
import com.tracek.domain.location.application.dto.TourImageResult;
import com.tracek.domain.location.infrastructure.config.TourApiProperties;
import java.util.ArrayList;
import java.util.List;
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

    private final RestClient tourApiRestClient;
    private final TourApiProperties properties;
    private final ObjectMapper objectMapper;

    public TourApiImageClient(
            @Qualifier("tourApiRestClient") RestClient tourApiRestClient,
            TourApiProperties properties,
            ObjectMapper objectMapper) {
        this.tourApiRestClient = tourApiRestClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<TourImageResult> getImages(Long externalContentId) {
        String rawBody =
                tourApiRestClient
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

        try {
            return parseImages(rawBody);
        } catch (Exception e) {
            throw new IllegalStateException("TourAPI 이미지 응답 파싱 실패: " + e.getMessage(), e);
        }
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
