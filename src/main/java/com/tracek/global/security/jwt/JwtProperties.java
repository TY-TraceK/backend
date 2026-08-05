package com.tracek.global.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "app.jwt")
@Setter
public class JwtProperties {

    private final String secret;
    private final Long accessExpirationTime;
    private final Long refreshExpirationTime;

    public JwtProperties(String secret, Long accessExpirationTime, Long refreshExpirationTime) {
        this.secret = secret;
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }
}
