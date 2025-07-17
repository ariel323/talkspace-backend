package com.alura_foro_api.foro_backend.security;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Lista de rutas públicas que no requieren autenticación
        if (isPublicPath(path) || "OPTIONS".equalsIgnoreCase(method)) {
            logger.debug("Permitiendo acceso público a: {} {}", method, path);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    
                    // Crear autenticación
                    org.springframework.security.core.Authentication authentication =
                            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                    username, null, java.util.Collections.emptyList());
                    
                    // Establecer contexto de seguridad
                    org.springframework.security.core.context.SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                    
                    logger.debug("Usuario autenticado: {}", username);
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                logger.warn("Error al validar token JWT: {}", e.getMessage());
            }
        }
        
        // Si llegamos aquí, la autenticación falló
        logger.warn("Acceso no autorizado a: {} {}", method, path);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Token JWT requerido o inválido\"}");
    }

    /**
     * Verifica si la ruta es pública y no requiere autenticación
     */
    private boolean isPublicPath(String path) {
        return path.startsWith("/auth/login") ||
               path.startsWith("/auth/register") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/v3/api-docs") ||
               path.startsWith("/swagger-ui") ||
               path.equals("/swagger-ui.html") ||
               path.startsWith("/swagger-ui/") ||
               path.equals("/swagger-ui/index.html") ||
               path.startsWith("/swagger-resources") ||
               path.startsWith("/webjars") ||
               path.equals("/favicon.ico") ||
               path.startsWith("/v3/api-docs/swagger-config") ||
               path.startsWith("/swagger-resources/configuration/ui") ||
               path.startsWith("/swagger-resources/configuration/security") ||
               path.startsWith("/actuator") || // Para Spring Boot Actuator si lo usas
               path.equals("/error"); // Para páginas de error
    }
}
