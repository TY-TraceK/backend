package com.tracek.global.config;

import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile({"local"})
public class LocalStorageWebConfig implements WebMvcConfigurer {

    private final String storagePath;

    public LocalStorageWebConfig(@Value("${storage.local.path}") String storagePath) {
        this.storagePath = storagePath;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String absolutePath = Paths.get(storagePath).toAbsolutePath().normalize().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
