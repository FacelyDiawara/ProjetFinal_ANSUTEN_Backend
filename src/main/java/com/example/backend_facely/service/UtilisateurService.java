package com.example.backend_facely.service;

import com.example.backend_facely.dto.Utilisateur;
import com.example.backend_facely.entity.*;
import com.example.backend_facely.enums.Role;
import com.example.backend_facely.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UtilisateurService {
    private final UtilisateurRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Utilisateur> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public Utilisateur findById(Long id) {
        return toDto(getEntity(id));
    }

    public Utilisateur update(Long id, Utilisateur dto) {
        var entity = getEntity(id);
        entity.setNom(dto.getNom());
        entity.setPrenom(dto.getPrenom());
        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(entity.getEmail())) {
            repository.findByEmail(dto.getEmail()).ifPresent(u -> {
                if (!u.getId().equals(id)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email déjà utilisé");
            });
            entity.setEmail(dto.getEmail());
        }
        if (dto.getRole() != null) entity.setRole(dto.getRole());
        return toDto(repository.save(entity));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable");
        repository.deleteById(id);
    }

    public com.example.backend_facely.entity.Utilisateur createInternal(String nom, String prenom, String email, String motDePasse, Role role) {
        if (repository.existsByEmail(email)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email déjà utilisé");
        boolean isActif = (role == Role.ADMIN);
        return repository.save(com.example.backend_facely.entity.Utilisateur.builder()
                .nom(nom).prenom(prenom).email(email)
                .motDePasse(passwordEncoder.encode(motDePasse))
                .role(role == null ? Role.ETUDIANT : role)
                .actif(isActif)
                .build());
    }

    public com.example.backend_facely.entity.Utilisateur getEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }

    public Utilisateur toDto(com.example.backend_facely.entity.Utilisateur e) {
        return Utilisateur.builder().id(e.getId()).nom(e.getNom()).prenom(e.getPrenom()).email(e.getEmail()).role(e.getRole()).actif(e.isActif()).build();
    }

    public Utilisateur activerCompte(Long id, boolean actif) {
        var entity = getEntity(id);
        entity.setActif(actif);
        return toDto(repository.save(entity));
    }
}
