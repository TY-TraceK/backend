package com.tracek.global.security.filter;

import com.tracek.global.exception.CustomException;
import com.tracek.global.response.SecurityErrorCode;
import com.tracek.global.security.WhitelistProperties;
import com.tracek.global.security.authentication.AuthenticationPrincipal;
import com.tracek.global.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final WhitelistProperties whitelistProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            UsernamePasswordAuthenticationToken authentication = getAuthenticationWithToken(token);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        List<String> permitAllUrls = whitelistProperties.getPermitAllUrls();
        for (String pattern : permitAllUrls) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }

        if ("GET".equalsIgnoreCase(method)) {
            return pathMatcher.match("/api/artists/**", path)
                    || pathMatcher.match("/api/locations/**", path)
                    || pathMatcher.match("/api/contents/**", path)
                    || pathMatcher.match("/api/search/**", path);
        }
        return false;
    }

    private String extractToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private UsernamePasswordAuthenticationToken getAuthenticationWithToken(String token) {
        try {
            Claims claims = jwtTokenProvider.getClaims(token);

            String userId = claims.getSubject();
            String userRole = String.valueOf(claims.get("role"));
            String userName = String.valueOf(claims.get("name"));

            AuthenticationPrincipal userDetails =
                    new AuthenticationPrincipal(Long.parseLong(userId), userName, userRole);
            return new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
        } catch (Exception e) {
            logger.error("jwt + ", e);
            throw new CustomException(SecurityErrorCode.INVALID_TOKEN);
        }
    }
}
