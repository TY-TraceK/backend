package com.tracek.global.security;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    private List<String> allowedOriginUrls;

    public CorsProperties(List<String> allowedOriginUrls) {
        this.allowedOriginUrls = allowedOriginUrls;
    }
}
