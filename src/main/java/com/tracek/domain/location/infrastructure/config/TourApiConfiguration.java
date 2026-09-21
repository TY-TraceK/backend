package com.tracek.domain.location.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

// 서비스키가 비어있어도 빈은 항상 생성한다. 키가 없으면 실제 호출 시 TourAPI가 에러를 반환하고,
// LocationFacade가 이를 잡아 DB로 폴백하므로 별도 조건부 등록이 필요 없다.
@Configuration
@EnableConfigurationProperties(TourApiProperties.class)
public class TourApiConfiguration {

    @Bean
    @Qualifier("tourApiRestClient")
    RestClient tourApiRestClient(RestClient.Builder builder, TourApiProperties properties) {
        return builder.clone().baseUrl(properties.baseUrl()).build();
    }
}
