package com.example.backend_facely.security;

import com.example.backend_facely.repository.UtilisateurRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UtilisateurRepository repository;
    public CustomUserDetailsService(UtilisateurRepository repository) { this.repository=repository; }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var u=repository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("Utilisateur introuvable"));
        return User.withUsername(u.getEmail()).password(u.getMotDePasse()).roles(u.getRole().name()).build();
    }
}
