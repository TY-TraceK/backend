package com.tracek.domain.location.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracek.domain.location.application.dto.TourLocationDetailResult;
import com.tracek.domain.location.infrastructure.TourApiResponseCache;
import com.tracek.domain.location.infrastructure.config.TourApiProperties;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class TourApiLocationDetailClientTest {

    private static final String DETAIL_RESPONSE =
            """
            {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},
            "body":{"items":{"item":{"contentid":"1277679","overview":"부산타워 설명","tel":"051-000-0000"}},
            "numOfRows":1,"pageNo":1,"totalCount":1}}}
            """;

    @Mock private TourApiResponseCache cache;

    private static final TourApiProperties PROPERTIES =
            new TourApiProperties(
                    "test-service-key",
                    "https://apis.data.go.kr/B551011/KorService2",
                    "ETC",
                    "TraceK");

    private MockRestServiceServer server;
    private TourApiLocationDetailClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(PROPERTIES.baseUrl());
        server = MockRestServiceServer.bindTo(builder).build();
        client =
                new TourApiLocationDetailClient(
                        builder.build(), PROPERTIES, new ObjectMapper(), cache);
    }

    @Test
    @DisplayName("단건 조회는 item이 객체로 내려와도 overview/tel을 파싱한다")
    void getDetail_singleObjectItem_success() {
        server.expect(requestTo(containsString("/detailCommon2")))
                .andRespond(
                        withSuccess(
                                """
                                {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},
                                "body":{"items":{"item":{"contentid":"1277679","overview":"부산타워 설명","tel":"051-000-0000"}},
                                "numOfRows":1,"pageNo":1,"totalCount":1}}}
                                """,
                                MediaType.APPLICATION_JSON));

        TourLocationDetailResult result = client.getDetail(1277679L);

        assertThat(result.getOverview()).isEqualTo("부산타워 설명");
        assertThat(result.getTel()).isEqualTo("051-000-0000");
    }

    @Test
    @DisplayName("item이 배열로 내려와도 첫 번째 항목을 사용한다")
    void getDetail_arrayItem_usesFirst() {
        server.expect(requestTo(containsString("/detailCommon2")))
                .andRespond(
                        withSuccess(
                                """
                                {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},
                                "body":{"items":{"item":[{"contentid":"1277679","overview":"부산타워 설명","tel":"051-000-0000"}]},
                                "numOfRows":1,"pageNo":1,"totalCount":1}}}
                                """,
                                MediaType.APPLICATION_JSON));

        TourLocationDetailResult result = client.getDetail(1277679L);

        assertThat(result.getOverview()).isEqualTo("부산타워 설명");
    }

    @Test
    @DisplayName("resultCode가 0000이 아니면 예외를 던진다")
    void getDetail_errorResultCode_throwsException() {
        server.expect(requestTo(containsString("/detailCommon2")))
                .andRespond(
                        withSuccess(
                                """
                                {"response":{"header":{"resultCode":"30","resultMsg":"SERVICE_KEY_IS_NOT_REGISTERED_ERROR"},
                                "body":{}}}
                                """,
                                MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.getDetail(1L)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("결과 없이 items가 빈 문자열로 내려오면 예외를 던진다")
    void getDetail_emptyItemsString_throwsException() {
        server.expect(requestTo(containsString("/detailCommon2")))
                .andRespond(
                        withSuccess(
                                """
                                {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},
                                "body":{"items":"","numOfRows":0,"pageNo":1,"totalCount":0}}}
                                """,
                                MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.getDetail(1L)).isInstanceOf(IllegalStateException.class);
        verify(cache, never()).set(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("캐시 hit이면 TourAPI를 호출하지 않고 캐시된 원본 응답을 파싱해서 반환한다")
    void getDetail_cacheHit_doesNotCallTourApi() {
        given(cache.get("tourapi:detail:1277679")).willReturn(Optional.of(DETAIL_RESPONSE));

        TourLocationDetailResult result = client.getDetail(1277679L);

        assertThat(result.getOverview()).isEqualTo("부산타워 설명");
        server.verify(); // expect를 걸지 않았으므로 요청이 1건이라도 나가면 실패
        verify(cache, never()).set(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("캐시 miss면 TourAPI 원본 응답을 24시간 TTL로 저장한다")
    void getDetail_cacheMiss_savesRawBodyWith24hTtl() {
        server.expect(requestTo(containsString("/detailCommon2")))
                .andRespond(withSuccess(DETAIL_RESPONSE, MediaType.APPLICATION_JSON));

        client.getDetail(1277679L);

        server.verify();
        verify(cache).set("tourapi:detail:1277679", DETAIL_RESPONSE, Duration.ofHours(24));
    }

    @Test
    @DisplayName("resultCode가 0000이 아닌 실패 응답은 캐시에 저장하지 않는다")
    void getDetail_errorResultCode_doesNotSaveCache() {
        server.expect(requestTo(containsString("/detailCommon2")))
                .andRespond(
                        withSuccess(
                                """
                                {"response":{"header":{"resultCode":"30","resultMsg":"SERVICE_KEY_IS_NOT_REGISTERED_ERROR"},
                                "body":{}}}
                                """,
                                MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.getDetail(1L)).isInstanceOf(IllegalStateException.class);
        verify(cache, never()).set(anyString(), anyString(), any());
    }
}
