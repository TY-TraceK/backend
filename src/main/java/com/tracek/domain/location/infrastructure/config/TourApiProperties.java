package com.tracek.domain.location.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.tour-api")
public record TourApiProperties(
        String serviceKey, String baseUrl, String mobileOs, String mobileApp) {

    private static final String DEFAULT_BASE_URL = "https://apis.data.go.kr/B551011/KorService2";
    private static final String DEFAULT_MOBILE_OS = "ETC";
    private static final String DEFAULT_MOBILE_APP = "TraceK";

    public TourApiProperties {
        baseUrl = defaultIfBlank(baseUrl, DEFAULT_BASE_URL);
        mobileOs = defaultIfBlank(mobileOs, DEFAULT_MOBILE_OS);
        mobileApp = defaultIfBlank(mobileApp, DEFAULT_MOBILE_APP);
    }

    private static String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
