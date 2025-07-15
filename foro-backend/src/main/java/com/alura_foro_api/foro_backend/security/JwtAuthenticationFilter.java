package com.alura_foro_api.foro_backend.security;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI().toLowerCase();

        if (
            path.startsWith("/auth/login") ||
            path.startsWith("/auth/register") ||
            path.startsWith("/v3/api-docs") ||
            path.equals("/v3/api-docs") ||
            path.startsWith("/swagger-ui") ||
            path.equals("/swagger-ui.html") ||
            path.startsWith("/swagger-resources") ||
            path.startsWith("/webjars") ||
            path.equals("/favicon.ico") ||
            path.startsWith("/v3/api-docs/swagger-config") ||
            path.startsWith("/swagger-resources/configuration/ui") ||
            path.startsWith("/swagger-resources/configuration/security") ||
            "options".equalsIgnoreCase(request.getMethod())
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                org.springframework.security.core.Authentication authentication =
                        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                username, null, java.util.Collections.emptyList());
                org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
