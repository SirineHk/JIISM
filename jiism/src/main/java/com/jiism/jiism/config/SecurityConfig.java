package com.jiism.jiism.config;

import com.jiism.jiism.security.CustomUserDetailsService;
import com.jiism.jiism.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            JwtFilter jwtFilter
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        return http

                // API REST stateless
                .csrf(csrf -> csrf.disable())

                // CORS Angular
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Pas de session HTTP
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Autorisations
                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // PUBLIC
                        // =========================

                                .requestMatchers(
                                        "/auth/**",
                                        "/verify-email/**",
                                        "/public/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**"
                                ).permitAll()

// Upload PDF : réservé à l'auteur
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.POST,
                                        "/files/upload-pdf"
                                ).hasRole("AUTEUR")

// Consultation des PDF
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.GET,
                                        "/files/**"
                                ).permitAll()
                        // =========================
                        // ADMIN
                        // =========================

                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // L'assignation est une opération ADMIN
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/article-assignments"
                        )
                        .hasRole("ADMIN")

                        // Les autres opérations d'une assignation
                        // concernent l'éditeur
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/article-assignments/*/start"
                        )
                        .hasRole("EDITEUR")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/article-assignments/*/complete"
                        )
                        .hasRole("EDITEUR")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/article-assignments/*/accept"
                        )
                        .hasRole("EDITEUR")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/article-assignments/*/reject"
                        )
                        .hasRole("EDITEUR")

                        // =========================
                        // REVIEWS
                        // =========================

                        .requestMatchers("/article-reviews/**")
                        .hasRole("EDITEUR")

                        // =========================
                        // AUTEUR
                        // =========================

                        .requestMatchers("/auteur/**")
                        .hasRole("AUTEUR")

                        // =========================
                        // EDITEUR
                        // =========================

                        .requestMatchers("/editeur/**")
                        .hasRole("EDITEUR")

                        // =========================
                        // ARTICLES
                        // =========================

                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/article"
                        )
                        .hasRole("AUTEUR")
                        .requestMatchers("/notifications/**").authenticated()

                        .requestMatchers("/announcements/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/announcements/public/**")
                        .permitAll()

                        // =========================
                        // TOUT LE RESTE
                        // =========================

                        .anyRequest()
                        .authenticated()
                )

                // JWT
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =========================================================
    // AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(
                userDetailsService
        );

        authProvider.setPasswordEncoder(
                passwordEncoder()
        );

        return authProvider;
    }

    // =========================================================
    // AUTHENTICATION MANAGER
    // =========================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }

    // =========================================================
    // CORS
    // =========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:4200",
                        "http://jiism-frontend-jiism.apps.dell64.dottn.com"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept"
                )
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}