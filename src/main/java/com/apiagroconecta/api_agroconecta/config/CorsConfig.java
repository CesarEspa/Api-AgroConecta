package com.apiagroconecta.api_agroconecta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

// CORS: Cross-Origin Resource Sharing.
// Solo los orígenes listados en CORS_ORIGINS (separados por coma) pueden
// llamar a la API desde el navegador. Por defecto: GitHub Pages y local.
@Configuration
public class CorsConfig {

    @Value("${app.cors.origins}")
    private String origins;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        List<String> allowed = Arrays.stream(origins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        allowed.forEach(config::addAllowedOriginPattern);

        // Métodos que usa el frontend (PATCH: cambio de estado de pedidos y usuarios)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Permite todos los headers, incluyendo Authorization: Bearer <token>
        config.addAllowedHeader("*");
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
