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
                .requestMatchers(HttpMethod.GET, "/api/eventos/publicados").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/eventos/*/publicado").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/eventos/*").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/eventos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/eventos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/eventos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/eventos/**").authenticated()
                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}