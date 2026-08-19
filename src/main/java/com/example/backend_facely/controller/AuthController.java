package com.example.backend_facely.controller;

import com.example.backend_facely.dto.AuthResponseDTO;
import com.example.backend_facely.dto.LoginDTO;
import com.example.backend_facely.entity.Utilisateur;
import com.example.backend_facely.enums.Role;
import com.example.backend_facely.repository.UtilisateurRepository;
import com.example.backend_facely.security.JwtService;
import com.example.backend_facely.service.UtilisateurService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository repository;
    private final UtilisateurService utilisateurService;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UtilisateurRepository repository,
            UtilisateurService utilisateurService,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.repository = repository;
        this.utilisateurService = utilisateurService;
        this.jwtService = jwtService;
    }

    // =========================================================
    // CONNEXION
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginDTO request) {

        System.out.println("=== LOGIN ===");
        System.out.println("Email reçu : " + request.getEmail());

        try {

            // Vérification de l'email et du mot de passe
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getMotDePasse()
                    )
            );

            System.out.println(
                    "=== AUTHENTIFICATION REUSSIE ==="
            );

            // Récupération de l'utilisateur
            Utilisateur user = repository
                    .findByEmail(request.getEmail())
                    .orElseThrow();

            // Génération de la réponse avec JWT
            AuthResponseDTO response = toResponse(user);

            System.out.println(
                    "=== JWT GENERE ==="
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            System.out.println(
                    "=== ERREUR AUTHENTIFICATION ==="
            );

            System.out.println(
                    "Type : " + e.getClass().getName()
            );

            System.out.println(
                    "Message : " + e.getMessage()
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            "Email ou mot de passe incorrect"
                    );
        }
    }

    // =========================================================
    // INSCRIPTION
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @RequestParam(defaultValue = "ETUDIANT") Role role,
            @RequestBody RegistrationRequest request) {

        /*
         * Pour des raisons de sécurité, un utilisateur
         * ne peut pas créer un compte ADMIN depuis
         * l'inscription publique.
         */
        Role safeRole =
                role == Role.ADMIN
                        ? Role.ETUDIANT
                        : role;

        Utilisateur user =
                utilisateurService.createInternal(
                        request.nom(),
                        request.prenom(),
                        request.email(),
                        request.motDePasse(),
                        safeRole
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(user));
    }

    // =========================================================
    // CRÉATION DE LA RÉPONSE JWT
    // =========================================================

    private AuthResponseDTO toResponse(
            Utilisateur user) {

        return AuthResponseDTO
                .builder()
                .token(
                        jwtService.generateToken(
                                user.getEmail(),
                                user.getRole().name()
                        )
                )
                .type("Bearer")
                .utilisateurId(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    // =========================================================
    // DTO POUR L'INSCRIPTION
    // =========================================================

    public record RegistrationRequest(
            String nom,
            String prenom,
            String email,
            String motDePasse
    ) {
    }
}