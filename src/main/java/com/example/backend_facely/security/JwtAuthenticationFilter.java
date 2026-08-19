package com.example.backend_facely.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // DEBUG : permet de voir les requêtes qui passent dans le filtre JWT
        System.out.println(
                "JWT FILTER -> "
                        + request.getMethod()
                        + " "
                        + request.getRequestURI()
        );

        String auth = request.getHeader("Authorization");

        // Si aucun token JWT n'est envoyé, on continue simplement la requête
        if (auth != null && auth.startsWith("Bearer ")) {

            String token = auth.substring(7);

            try {

                String username = jwtService.extractUsername(token);

                if (username != null
                        && SecurityContextHolder
                                .getContext()
                                .getAuthentication() == null) {

                    UserDetails user =
                            userDetailsService.loadUserByUsername(username);

                    if (jwtService.isTokenValid(
                            token,
                            user.getUsername())) {

                        var authentication =
                                new UsernamePasswordAuthenticationToken(
                                        user,
                                        null,
                                        user.getAuthorities()
                                );

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource()
                                        .buildDetails(request)
                        );

                        SecurityContextHolder
                                .getContext()
                                .setAuthentication(authentication);
                    }
                }

            } catch (Exception e) {

                // DEBUG : afficher l'erreur JWT au lieu de l'ignorer
                e.printStackTrace();
            }
        }

        // Très important : continuer vers Spring Security
        filterChain.doFilter(request, response);
    }
}
