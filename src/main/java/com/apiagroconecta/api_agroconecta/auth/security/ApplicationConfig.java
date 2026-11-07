package com.apiagroconecta.api_agroconecta.auth.security;

import com.apiagroconecta.api_agroconecta.auth.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;



@Configuration
public class ApplicationConfig {

    private final UsuarioRepository usuarioRepository;

    public ApplicationConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // UserDetailsService: Spring Security llama a este bean cuando necesita
    // cargar un usuario por su nombre de usuario (en nuestro caso, el email).
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuarioRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + username
                ));
    }

    // AuthenticationProvider: el componente que verifica credenciales.
    // DaoAuthenticationProvider usa UserDetailsService para cargar el usuario
    // y PasswordEncoder para verificar que la contraseña ingresada
    // coincide con el hash guardado en la base de datos.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // AuthenticationManager: el coordinador del proceso de autenticación.
    // AuthController lo usará para ejecutar el login: recibe email + contraseña
    // y delega la verificación al AuthenticationProvider.
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // PasswordEncoder: define cómo se hashean las contraseñas.
    // BCrypt agrega un salt aleatorio, lo que hace que el mismo texto
    // produzca hashes distintos en cada llamada.
    // Al verificar, BCrypt extrae el salt del hash guardado y lo aplica
    // a la contraseña ingresada para comparar.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}