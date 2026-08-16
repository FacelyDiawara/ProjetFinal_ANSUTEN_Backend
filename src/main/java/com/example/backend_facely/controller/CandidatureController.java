package com.example.backend_facely.controller;

import com.example.backend_facely.dto.Candidature;
import com.example.backend_facely.enums.StatutCandidature;
import com.example.backend_facely.service.CandidatureService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/candidatures")
public class CandidatureController {
    private final CandidatureService service;
    public CandidatureController(CandidatureService service){this.service=service;}
    @GetMapping public ResponseEntity<List<Candidature>> getAll(@RequestParam(required=false) StatutCandidature statut,@RequestParam(required=false) Long etudiantId,@RequestParam(required=false) Long offreStageId){return ResponseEntity.ok(service.findAll(statut,etudiantId,offreStageId));}
    @GetMapping("/{id}") public ResponseEntity<Candidature> getById(@PathVariable Long id){return ResponseEntity.ok(service.findById(id));}
    @PostMapping public ResponseEntity<Candidature> create(@Valid @RequestBody Candidature dto){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));}
    @PutMapping("/{id}") public ResponseEntity<Candidature> update(@PathVariable Long id,@Valid @RequestBody Candidature dto){return ResponseEntity.ok(service.update(id,dto));}
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}
}
