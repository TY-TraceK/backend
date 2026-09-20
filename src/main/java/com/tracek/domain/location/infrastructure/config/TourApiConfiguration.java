package com.tracek.domain.location.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(TourApiProperties.class)
@ConditionalOnProperty(prefix = "app.tour-api", name = "service-key")
public class TourApiConfiguration {

    @Bean
    @Qualifier("tourApiRestClient")
    RestClient tourApiRestClient(RestClient.Builder builder, TourApiProperties properties) {
        return builder.clone().baseUrl(properties.baseUrl()).build();
    }
}
