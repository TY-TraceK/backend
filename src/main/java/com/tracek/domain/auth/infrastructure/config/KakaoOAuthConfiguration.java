package com.tracek.domain.auth.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(KakaoOAuthProperties.class)
@ConditionalOnProperty(prefix = "app.auth.kakao", name = "client-id")
public class KakaoOAuthConfiguration {

    @Bean
    @Qualifier("kakaoAuthorizationRestClient")
    RestClient kakaoAuthorizationRestClient(
            RestClient.Builder builder, KakaoOAuthProperties properties) {
        return builder.clone().baseUrl(properties.authorizationBaseUrl()).build();
    }

    @Bean
    @Qualifier("kakaoApiRestClient")
    RestClient kakaoApiRestClient(RestClient.Builder builder, KakaoOAuthProperties properties) {
        return builder.clone().baseUrl(properties.apiBaseUrl()).build();
    }
}
