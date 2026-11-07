package com.apiagroconecta.api_agroconecta.auth.security;

import com.apiagroconecta.api_agroconecta.auth.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    public JwtFilter(JwtUtil jwtUtil, UsuarioRepository usuarioRepository) {
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Leer el header Authorization de la solicitud entrante.
        // El formato esperado es: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        final String authHeader = request.getHeader("Authorization");

        // Si el header no existe o no empieza con "Bearer ",
        // esta solicitud no tiene token JWT — pasar al siguiente filtro.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer el token removiendo el prefijo "Bearer " (7 caracteres).
        final String token = authHeader.substring(7);

        // Extraer el email del payload del token.
        final String email = jwtUtil.extractEmail(token);

        // Solo proceder si tenemos un email y el usuario aún no está
        // autenticado en el contexto de seguridad de esta solicitud.
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargar el usuario desde la base de datos.
            UserDetails userDetails = usuarioRepository
                    .findByEmail(email)
                    .orElse(null);

            // Validar que el usuario existe y que el token es válido.
            if (userDetails != null && jwtUtil.isTokenValid(token, userDetails)) {

                // Crear el objeto de autenticación que Spring Security necesita.
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // credentials: null porque ya validamos con el token
                                userDetails.getAuthorities()
                        );

                // Agregar detalles de la solicitud al objeto de autenticación.
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Registrar la autenticación en el SecurityContext.
                // A partir de este punto, Spring Security sabe quién es este usuario
                // y qué roles tiene para el resto del Filter Chain.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuar con el siguiente filtro en la cadena.
        filterChain.doFilter(request, response);
    }
}