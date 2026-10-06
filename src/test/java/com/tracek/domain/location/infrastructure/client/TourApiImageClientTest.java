package com.tracek.domain.location.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracek.domain.location.application.dto.TourImageResult;
import com.tracek.domain.location.infrastructure.TourApiResponseCache;
import com.tracek.domain.location.infrastructure.config.TourApiProperties;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class TourApiImageClientTest {

    private static final String IMAGES_RESPONSE =
            """
            {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},
            "body":{"items":{"item":[
              {"contentid":"1277679","originimgurl":"http://img1.jpg","smallimageurl":"http://img1_s.jpg"},
              {"contentid":"1277679","originimgurl":"http://img2.jpg","smallimageurl":"http://img2_s.jpg"}
            ]},"numOfRows":2,"pageNo":1,"totalCount":2}}}
            """;

    // 결과가 1건이면 item이 배열이 아니라 객체로 내려오는 공공데이터포털 특성
    private static final String SINGLE_IMAGE_RESPONSE =
            """
            {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},
            "body":{"items":{"item":
              {"contentid":"123","originimgurl":"http://only.jpg","smallimageurl":"http://only_s.jpg"}
            },"numOfRows":1,"pageNo":1,"totalCount":1}}}
            """;

    private static final String EMPTY_ITEMS_RESPONSE =
            """
            {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},
            "body":{"items":"","numOfRows":0,"pageNo":1,"totalCount":0}}}
            """;

    private static final String ERROR_RESPONSE =
            """
            {"response":{"header":{"resultCode":"30","resultMsg":"SERVICE_KEY_IS_NOT_REGISTERED_ERROR"},
            "body":{}}}
            """;

    @Mock private TourApiResponseCache cache;

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
        client = new TourApiImageClient(builder.build(), PROPERTIES, new ObjectMapper(), cache);
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

    @Test
    @DisplayName("캐시 hit이면 TourAPI를 호출하지 않고 캐시된 원본 응답을 파싱해서 반환한다")
    void getImages_cacheHit_doesNotCallTourApi() {
        given(cache.get("tourapi:image:1277679")).willReturn(Optional.of(IMAGES_RESPONSE));

        List<TourImageResult> result = client.getImages(1277679L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getImageUrl()).isEqualTo("http://img1.jpg");
        server.verify(); // expect를 걸지 않았으므로 요청이 1건이라도 나가면 실패
        verify(cache, never()).set(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("캐시 miss면 TourAPI 원본 응답을 24시간 TTL로 저장한다")
    void getImages_cacheMiss_savesRawBodyWith24hTtl() {
        server.expect(requestTo(containsString("/detailImage2")))
                .andRespond(withSuccess(IMAGES_RESPONSE, MediaType.APPLICATION_JSON));

        client.getImages(1277679L);

        server.verify();
        verify(cache).set("tourapi:image:1277679", IMAGES_RESPONSE, Duration.ofHours(24));
    }

    @Test
    @DisplayName("이미지가 1건이라 item이 객체로 와도 이미지가 있는 것으로 보고 24시간 TTL로 저장한다")
    void getImages_singleItemObject_savesWith24hTtl() {
        server.expect(requestTo(containsString("/detailImage2")))
                .andRespond(withSuccess(SINGLE_IMAGE_RESPONSE, MediaType.APPLICATION_JSON));

        List<TourImageResult> result = client.getImages(123L);

        assertThat(result).hasSize(1);
        verify(cache).set(eq("tourapi:image:123"), anyString(), eq(Duration.ofHours(24)));
    }

    @Test
    @DisplayName("성공 응답이지만 이미지가 없으면 1시간 TTL로 짧게 저장한다")
    void getImages_emptyItems_savesWith1hTtl() {
        server.expect(requestTo(containsString("/detailImage2")))
                .andRespond(withSuccess(EMPTY_ITEMS_RESPONSE, MediaType.APPLICATION_JSON));

        List<TourImageResult> result = client.getImages(999L);

        assertThat(result).isEmpty();
        verify(cache).set(eq("tourapi:image:999"), anyString(), eq(Duration.ofHours(1)));
    }

    @Test
    @DisplayName("resultCode가 0000이 아닌 실패 응답은 캐시에 저장하지 않는다")
    void getImages_errorResultCode_doesNotSaveCache() {
        server.expect(requestTo(containsString("/detailImage2")))
                .andRespond(withSuccess(ERROR_RESPONSE, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.getImages(1L)).isInstanceOf(IllegalStateException.class);
        verify(cache, never()).set(anyString(), anyString(), any());
    }
}
