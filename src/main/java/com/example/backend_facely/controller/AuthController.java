package com.example.backend_facely.controller;

import com.example.backend_facely.dto.*;
import com.example.backend_facely.entity.Utilisateur;
import com.example.backend_facely.enums.Role;
import com.example.backend_facely.repository.UtilisateurRepository;
import com.example.backend_facely.security.JwtService;
import com.example.backend_facely.service.UtilisateurService;

import jakarta.validation.Valid;

import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository repository;
    private final UtilisateurService utilisateurService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthenticationManager authenticationManager,
            UtilisateurRepository repository,
            UtilisateurService utilisateurService,
            JwtService jwtService,
            PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.repository = repository;
        this.utilisateurService = utilisateurService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // CONNEXION
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginDTO request) {

        System.out.println("=== LOGIN ===");
        System.out.println("Email reçu : " + request.getEmail());
        System.out.println(
                "Mot de passe reçu : "
                        + (request.getMotDePasse() != null
                        ? "OUI"
                        : "NON")
        );

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getMotDePasse()
                    )
            );

            System.out.println(
                    "=== AUTHENTIFICATION REUSSIE ==="
            );

            var user = repository
                    .findByEmail(request.getEmail())
                    .orElseThrow();

            AuthResponseDTO response = toResponse(user);

            System.out.println("=== JWT GENERE ===");

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            System.out.println(
                    "=== ERREUR AUTHENTIFICATION ==="
            );

            System.out.println(
                    "Type : "
                            + e.getClass().getName()
            );

            System.out.println(
                    "Message : "
                            + e.getMessage()
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
            @RequestParam(defaultValue = "ETUDIANT")
            Role role,
            @RequestBody RegistrationRequest request) {

        // Pour des raisons de sécurité,
        // l'inscription publique ne peut pas créer un ADMIN.
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
    // RÉINITIALISATION TEMPORAIRE DU MOT DE PASSE ADMIN
    // =========================================================
    //
    // À utiliser UNE SEULE FOIS pour résoudre le problème
    // du mot de passe de l'administrateur.
    //
    // Email :
    // facely@diawara.com
    //
    // Nouveau mot de passe temporaire :
    // admin123
    //
    // =========================================================

    @GetMapping("/reset-admin")
    public ResponseEntity<String> resetAdmin() {

        String email = "facely@diawara.com";

        String nouveauMotDePasse = "admin123";

        try {

            Utilisateur user =
                    repository
                            .findByEmail(email)
                            .orElseThrow(
                                    () -> new RuntimeException(
                                            "Administrateur introuvable"
                                    )
                            );

            // Encodage BCrypt du nouveau mot de passe
            String motDePasseEncode =
                    passwordEncoder.encode(
                            nouveauMotDePasse
                    );

            user.setMotDePasse(
                    motDePasseEncode
            );

            // On s'assure également que l'utilisateur
            // possède bien le rôle ADMIN.
            user.setRole(Role.ADMIN);

            repository.save(user);

            System.out.println(
                    "===================================="
            );

            System.out.println(
                    "ADMIN RÉINITIALISÉ AVEC SUCCÈS"
            );

            System.out.println(
                    "Email : " + email
            );

            System.out.println(
                    "Nouveau mot de passe : "
                            + nouveauMotDePasse
            );

            System.out.println(
                    "===================================="
            );

            return ResponseEntity.ok(
                    "Mot de passe administrateur "
                            + "réinitialisé avec succès."
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            "Erreur lors de la "
                                    + "réinitialisation : "
                                    + e.getMessage()
                    );
        }
    }


    // =========================================================
    // CONVERSION UTILISATEUR -> RÉPONSE JWT
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
    // DTO INSCRIPTION
    // =========================================================

    public record RegistrationRequest(
            String nom,
            String prenom,
            String email,
            String motDePasse
    ) {
    }
}