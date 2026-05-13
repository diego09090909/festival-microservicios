package com.festival.ms_evento.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// SecurityConfig de ms-eventos: protege endpoints segun el rol del JWT
// No necesita PasswordEncoder ni AuthenticationManager (no hace login)
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sess ->
                sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                // Consultas publicas: cualquiera puede ver eventos publicados
                .requestMatchers(HttpMethod.GET, "/api/eventos/publicados").permitAll()

                // Endpoint para Feign (otros microservicios verifican estado)
                .requestMatchers(HttpMethod.GET, "/api/eventos/*/publicado").permitAll()

                // Solo ADMIN puede crear, editar o cambiar estado de eventos
                .requestMatchers(HttpMethod.POST, "/api/eventos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/eventos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/eventos/**").hasRole("ADMIN")

                // Ver detalle de cualquier evento requiere estar autenticado
                .requestMatchers(HttpMethod.GET, "/api/eventos/**").authenticated()

                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
