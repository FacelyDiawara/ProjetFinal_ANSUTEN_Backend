package com.example.backend_facely.controller;

import com.example.backend_facely.dto.Utilisateur;
import com.example.backend_facely.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateureController {
    private final UtilisateurService service;
    public UtilisateureController(UtilisateurService service){this.service=service;}
    @GetMapping public ResponseEntity<List<Utilisateur>> getAll(){return ResponseEntity.ok(service.findAll());}
    @GetMapping("/{id}") public ResponseEntity<Utilisateur> getById(@PathVariable Long id){return ResponseEntity.ok(service.findById(id));}
    @PutMapping("/{id}") public ResponseEntity<Utilisateur> update(@PathVariable Long id,@Valid @RequestBody Utilisateur dto){return ResponseEntity.ok(service.update(id,dto));}
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}
}
