package com.example.backend_facely.controller;

import com.example.backend_facely.dto.Entreprise;
import com.example.backend_facely.enums.StatutValidation;
import com.example.backend_facely.service.EntrepriseService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/entreprises")
public class EntrepriseController {
    private final EntrepriseService service;
    public EntrepriseController(EntrepriseService service){this.service=service;}
    @GetMapping public ResponseEntity<List<Entreprise>> getAll(@RequestParam(required=false) String secteur){return ResponseEntity.ok(secteur==null?service.findAll():service.findBySecteur(secteur));}
    @GetMapping("/{id}") public ResponseEntity<Entreprise> getById(@PathVariable Long id){return ResponseEntity.ok(service.findById(id));}
    @PostMapping public ResponseEntity<Entreprise> create(@Valid @RequestBody Entreprise dto){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));}
    @PutMapping("/{id}") public ResponseEntity<Entreprise> update(@PathVariable Long id,@Valid @RequestBody Entreprise dto){return ResponseEntity.ok(service.update(id,dto));}
    @PutMapping("/{id}/validation") public ResponseEntity<Entreprise> validation(@PathVariable Long id, @RequestParam StatutValidation statut){
        var dto=service.findById(id); dto.setStatutValidation(statut);
        return ResponseEntity.ok(service.updateValidation(id, statut));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}
}
