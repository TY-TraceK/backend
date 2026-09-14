package com.tracek.global.security;

import com.tracek.global.security.filter.JwtAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties({CorsProperties.class, WhitelistProperties.class})
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsProperties corsProperties;
    private final WhitelistProperties whitelistProperties; // Whitelist 프로퍼티 주입

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Whitelist 리스트를 String 배열로 변환
        String[] permitAllPatterns = whitelistProperties.getPermitAllUrls().toArray(new String[0]);

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        // 환경변수/yml에서 불러온 Whitelist URL 전체 permitAll 처리
                                        .requestMatchers(permitAllPatterns)
                                        .permitAll()

                                        // 특정 HTTP Method가 필요한 Public API들
                                        .requestMatchers(
                                                HttpMethod.GET,
                                                "/api/artists/**",
                                                "/api/locations/**",
                                                "/api/contents/**",
                                                "/api/search/**")
                                        .permitAll()

                                        // 인증 필요한 요청들
                                        .requestMatchers("/api/users/**")
                                        .authenticated()
                                        .anyRequest()
                                        .authenticated())
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(corsProperties.getAllowedOriginUrls());
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
