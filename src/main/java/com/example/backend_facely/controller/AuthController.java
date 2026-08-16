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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository repository;
    private final UtilisateurService utilisateurService;
    private final JwtService jwtService;
    public AuthController(AuthenticationManager authenticationManager,UtilisateurRepository repository,UtilisateurService utilisateurService,JwtService jwtService){this.authenticationManager=authenticationManager;this.repository=repository;this.utilisateurService=utilisateurService;this.jwtService=jwtService;}

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginDTO request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getMotDePasse()));
        var user=repository.findByEmail(request.getEmail()).orElseThrow();
        return ResponseEntity.ok(toResponse(user));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestParam(defaultValue="ETUDIANT") Role role, @RequestBody RegistrationRequest request){
        Role safeRole = role == Role.ADMIN ? Role.ETUDIANT : role;
        Utilisateur user=utilisateurService.createInternal(request.nom(),request.prenom(),request.email(),request.motDePasse(),safeRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    private AuthResponseDTO toResponse(Utilisateur user){return AuthResponseDTO.builder().token(jwtService.generateToken(user.getEmail(),user.getRole().name())).type("Bearer").utilisateurId(user.getId()).nom(user.getNom()).prenom(user.getPrenom()).email(user.getEmail()).role(user.getRole()).build();}

    public record RegistrationRequest(String nom,String prenom,String email,String motDePasse){}
}
