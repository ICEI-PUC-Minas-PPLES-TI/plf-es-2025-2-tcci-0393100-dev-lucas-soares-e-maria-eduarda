package br.pucminas.graphtest.infrastructure.config;

import br.pucminas.graphtest.adapters.inbound.security.JwtAuthenticationFilter;
import br.pucminas.graphtest.adapters.inbound.security.JwtAuthorizationFilter;
import br.pucminas.graphtest.application.port.input.security.GenerateTokenUseCase;
import br.pucminas.graphtest.application.port.input.security.ResolveAuthenticatedUserByTokenUseCase;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static br.pucminas.graphtest.shared.logging.LogTopics.SEGURANCA_CONFIG;
import static org.springframework.http.HttpMethod.POST;

@Slf4j(topic = SEGURANCA_CONFIG)
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final GenerateTokenUseCase generateTokenUseCase;
    private final ResolveAuthenticatedUserByTokenUseCase resolveAuthenticatedUserByTokenUseCase;

    @Bean
    public SecurityFilterChain filterChain(@NotNull HttpSecurity httpSecurity, PasswordEncoder passwordEncoder)
            throws Exception {
        log.info(">>> filterChain: iniciando camada de seguranca Filter Chain");

        AuthenticationManagerBuilder authenticationManagerBuilder =
                httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationManager(authenticationManager)
                .addFilter(new JwtAuthenticationFilter(authenticationManager, generateTokenUseCase))
                .addFilter(new JwtAuthorizationFilter(authenticationManager, resolveAuthenticatedUserByTokenUseCase))
                .authorizeHttpRequests(request -> {
                    request.requestMatchers(SecurityRequestPaths.CAMINHOS_PUBLICOS).permitAll();
                    request.requestMatchers(POST, SecurityRequestPaths.CAMINHOS_PUBLICOS_POST).permitAll();
                    request.anyRequest().authenticated();
                })
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info(">>> corsConfigurationSource: iniciando configuracao de Cors");

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
