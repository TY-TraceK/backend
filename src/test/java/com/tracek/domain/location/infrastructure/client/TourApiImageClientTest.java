package com.tracek.domain.location.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracek.domain.location.application.dto.TourImageResult;
import com.tracek.domain.location.infrastructure.config.TourApiProperties;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class TourApiImageClientTest {

    private static final TourApiProperties PROPERTIES =
            new TourApiProperties(
                    "test-service-key",
                    "https://apis.data.go.kr/B551011/KorService2",
                    "ETC",
                    "TraceK");

    private MockRestServiceServer server;
    private TourApiImageClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(PROPERTIES.baseUrl());
        server = MockRestServiceServer.bindTo(builder).build();
        client = new TourApiImageClient(builder.build(), PROPERTIES, new ObjectMapper());
    }

    @Test
    @DisplayName("정상 응답이면 이미지 목록을 파싱해서 반환한다")
    void getImages_success() {
        server.expect(requestTo(containsString("/detailImage2")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withSuccess(
                                """
                                {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},
                                "body":{"items":{"item":[
                                  {"contentid":"1277679","originimgurl":"http://img1.jpg","smallimageurl":"http://img1_s.jpg"},
                                  {"contentid":"1277679","originimgurl":"http://img2.jpg","smallimageurl":"http://img2_s.jpg"}
                                ]},"numOfRows":2,"pageNo":1,"totalCount":2}}}
                                """,
                                MediaType.APPLICATION_JSON));

        List<TourImageResult> result = client.getImages(1277679L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getImageUrl()).isEqualTo("http://img1.jpg");
        assertThat(result.get(0).getSmallImageUrl()).isEqualTo("http://img1_s.jpg");
    }

    @Test
    @DisplayName("결과가 없어 items가 빈 문자열로 내려오면 빈 리스트를 반환한다")
    void getImages_emptyItemsString_returnsEmptyList() {
        server.expect(requestTo(containsString("/detailImage2")))
                .andRespond(
                        withSuccess(
                                """
                                {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},
                                "body":{"items":"","numOfRows":0,"pageNo":1,"totalCount":0}}}
                                """,
                                MediaType.APPLICATION_JSON));

        List<TourImageResult> result = client.getImages(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("resultCode가 0000이 아니면 예외를 던진다")
    void getImages_errorResultCode_throwsException() {
        server.expect(requestTo(containsString("/detailImage2")))
                .andRespond(
                        withSuccess(
                                """
                                {"response":{"header":{"resultCode":"30","resultMsg":"SERVICE_KEY_IS_NOT_REGISTERED_ERROR"},
                                "body":{}}}
                                """,
                                MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.getImages(1L)).isInstanceOf(IllegalStateException.class);
    }
}
