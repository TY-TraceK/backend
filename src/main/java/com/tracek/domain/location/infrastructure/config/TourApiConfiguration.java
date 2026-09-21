package com.tracek.domain.location.infrastructure.config;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

// 서비스키가 비어있어도 빈은 항상 생성한다. 키가 없으면 실제 호출 시 TourAPI가 에러를 반환하고,
// LocationFacade가 이를 잡아 DB로 폴백하므로 별도 조건부 등록이 필요 없다.
//
// HTTP/1.1을 명시하는 이유: 기본(HTTP/2 협상)으로는 TourAPI 게이트웨이가 배포 서버(EC2)에서 온 요청에
// "APPLICATION_ERROR"(OpenAPI_ServiceResponse)를 반환하는 현상을 확인했다. 동일 요청을 curl(HTTP/1.1)로
// 보내면 항상 성공하므로, JDK HttpClient의 HTTP/2 협상 문제로 판단해 HTTP/1.1로 고정한다.
@Configuration
@EnableConfigurationProperties(TourApiProperties.class)
public class TourApiConfiguration {

    @Bean
    @Qualifier("tourApiRestClient")
    RestClient tourApiRestClient(RestClient.Builder builder, TourApiProperties properties) {
        HttpClient httpClient =
                HttpClient.newBuilder()
                        .version(HttpClient.Version.HTTP_1_1)
                        .connectTimeout(Duration.ofSeconds(5))
                        .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        return builder.clone()
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }
}
