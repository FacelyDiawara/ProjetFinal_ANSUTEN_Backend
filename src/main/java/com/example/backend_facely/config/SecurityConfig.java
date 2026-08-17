package com.example.backend_facely.config;

import com.example.backend_facely.security.CustomUserDetailsService;
import com.example.backend_facely.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtFilter,
            CustomUserDetailsService userDetailsService) {

        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean 
    public AuthenticationProvider authenticationProvider(
        PasswordEncoder encoder) {

        DaoAuthenticationProvider provider =
            new DaoAuthenticationProvider(userDetailsService);

            provider.setPasswordEncoder(encoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(cors -> {})

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authenticationProvider(
                authenticationProvider(passwordEncoder())
            )

            .authorizeHttpRequests(auth -> auth

                // Authentification
                .requestMatchers(
                    "/api/auth/**",
                    "/error"
                ).permitAll()

                // Consultation publique des offres
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/offres/**"
                ).permitAll()

                // Consultation publique des entreprises
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/entreprises/**"
                ).permitAll()

                // Étudiants
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/etudiants/**"
                ).hasAnyRole(
                    "ETUDIANT",
                    "ADMIN"
                )

                // Gestion des offres
                .requestMatchers(
                    "/api/offres/**"
                ).hasAnyRole(
                    "ENTREPRISE",
                    "ADMIN"
                )

                // Candidatures
                .requestMatchers(
                    "/api/candidatures/**"
                ).hasAnyRole(
                    "ETUDIANT",
                    "ENTREPRISE",
                    "ADMIN"
                )

                // Validation d'une entreprise
                .requestMatchers(
                    "/api/entreprises/*/validation"
                ).hasRole("ADMIN")

                // Gestion des entreprises
                .requestMatchers(
                    "/api/entreprises/**"
                ).hasAnyRole(
                    "ENTREPRISE",
                    "ADMIN"
                )

                // Gestion des utilisateurs
                .requestMatchers(
                    "/api/utilisateurs/**"
                ).hasRole("ADMIN")

                // Tout le reste
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}