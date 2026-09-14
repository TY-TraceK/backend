package com.tracek.global.security;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "app.security")
@Setter
public class WhitelistProperties {

    private final List<String> permitAllUrls;

    public WhitelistProperties(List<String> permitAllUrls) {
        this.permitAllUrls = permitAllUrls;
    }
}
