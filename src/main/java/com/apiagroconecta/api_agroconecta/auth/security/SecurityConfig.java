package com.apiagroconecta.api_agroconecta.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @EnableWebSecurity: activa el módulo de seguridad web de Spring Security.
// Esta clase define el SecurityFilterChain: las reglas que determinan
// qué solicitudes pasan y cuáles se bloquean, y con qué condiciones.
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Activa @PreAuthorize, @PostAuthorize, etc. a nivel de método
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtFilter jwtFilter,
                          AuthenticationProvider authenticationProvider) {
        this.jwtFilter = jwtFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
    .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // Swagger: público durante el desarrollo.
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Endpoints de autenticación: públicos — son la puerta de entrada.
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Solo ADMIN
                        .requestMatchers("/api/v1/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/categorias/**", "/api/v1/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/categorias/**", "/api/v1/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/categorias/**", "/api/v1/productos/**").hasRole("ADMIN")

                        // ── CATÁLOGO PÚBLICO ──────────────────────────────────────────────
                        // Cualquier visitante (con o sin cuenta) puede ver:
                        //   · El catálogo de productos activos: GET /api/v1/productos
                        //   · Los productos en promoción:        GET /api/v1/productos/promociones
                        //   · Las categorías disponibles:        GET /api/v1/categorias/**
                        // El backend garantiza que solo retorna activos (findByActivoTrue)
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos", "/api/v1/productos/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/promociones").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categorias", "/api/v1/categorias/**").permitAll()

                        // Catálogo completo (activos + inactivos) solo para ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/admin").hasRole("ADMIN")

                        // Lectura de producto individual para ADMIN y CLIENTE autenticado
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").hasAnyRole("ADMIN", "CLIENTE")

                        // Pedidos: reglas específicas primero (orden importa)
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/mis-pedidos").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos").hasRole("CLIENTE")
                        .requestMatchers("/api/v1/pedidos/**").hasRole("ADMIN")

                        // Todo lo demás requiere autenticación.
                        .anyRequest().authenticated()
                )

                // Registrar el AuthenticationProvider que usa la BD.
                .authenticationProvider(authenticationProvider)

                // Insertar JwtFilter antes del filtro estándar de usuario/contraseña.
                // Así, cada solicitud primero pasa por la validación del token JWT.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}