import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
package com.example.backend_facely.config;

import com.example.backend_facely.security.JwtAuthenticationFilter;
import com.example.backend_facely.security.CustomUserDetailsService;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
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
    public SecurityConfig(JwtAuthenticationFilter jwtFilter,CustomUserDetailsService userDetailsService){this.jwtFilter=jwtFilter;this.userDetailsService=userDetailsService;}

    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}

    @Bean AuthenticationProvider authenticationProvider(PasswordEncoder encoder){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{return configuration.getAuthenticationManager();}

    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(csrf->csrf.disable())
            .cors(cors->{})
            .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider(passwordEncoder()))
            .authorizeHttpRequests(auth->auth
                .requestMatchers("/api/auth/**","/error").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/offres/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/entreprises/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/etudiants/**").hasAnyRole("ETUDIANT","ADMIN")
                .requestMatchers("/api/offres/**").hasAnyRole("ENTREPRISE","ADMIN")
                .requestMatchers("/api/candidatures/**").hasAnyRole("ETUDIANT","ENTREPRISE","ADMIN")
                .requestMatchers("/api/entreprises/*/validation").hasRole("ADMIN")
                .requestMatchers("/api/entreprises/**").hasAnyRole("ENTREPRISE","ADMIN")
                .requestMatchers("/api/utilisateurs/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
