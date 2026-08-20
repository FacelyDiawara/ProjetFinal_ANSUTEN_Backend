package com.example.backend_facely.controller;

import com.example.backend_facely.dto.Entreprise;
import com.example.backend_facely.enums.StatutValidation;
import com.example.backend_facely.service.EntrepriseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entreprises")
public class EntrepriseController {

    private final EntrepriseService service;

    public EntrepriseController(EntrepriseService service) {
        this.service = service;
    }

    /**
     * Récupérer toutes les entreprises.
     * Possibilité de filtrer par secteur.
     */
    @GetMapping
    public ResponseEntity<List<Entreprise>> getAll(
            @RequestParam(required = false) String secteur) {

        if (secteur == null || secteur.isBlank()) {
            return ResponseEntity.ok(service.findAll());
        }

        return ResponseEntity.ok(service.findBySecteur(secteur));
    }

    /**
     * Récupérer une entreprise par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Entreprise> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Créer une entreprise.
     *
     * utilisateurId peut être envoyé par Angular.
     * Si utilisateurId est absent, le backend récupère
     * automatiquement l'utilisateur connecté via le JWT.
     */
    @PostMapping
    public ResponseEntity<Entreprise> create(
            @Valid @RequestBody Entreprise dto) {

        Entreprise entreprise = service.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(entreprise);
    }

    /**
     * Modifier une entreprise.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Entreprise> update(
            @PathVariable Long id,
            @Valid @RequestBody Entreprise dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    /**
     * Valider ou rejeter une entreprise.
     */
    @PutMapping("/{id}/validation")
    public ResponseEntity<Entreprise> validation(
            @PathVariable Long id,
            @RequestParam StatutValidation statut) {

        return ResponseEntity.ok(
                service.updateValidation(id, statut)
        );
    }

    /**
     * Supprimer une entreprise.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}