package com.example.backend_facely.controller;

import com.example.backend_facely.dto.OffreStage;
import com.example.backend_facely.enums.StatuOffre;
import com.example.backend_facely.service.OffreStageService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/offres")
public class OffreStageController {
    private final OffreStageService service;
    public OffreStageController(OffreStageService service){this.service=service;}
    @GetMapping public ResponseEntity<List<OffreStage>> getAll(@RequestParam(required=false) String secteur,@RequestParam(required=false) String titre,@RequestParam(required=false) StatuOffre statut){return ResponseEntity.ok(service.findAll(secteur,titre,statut));}
    @GetMapping("/{id}") public ResponseEntity<OffreStage> getById(@PathVariable Long id){return ResponseEntity.ok(service.findById(id));}
    @PostMapping public ResponseEntity<OffreStage> create(@Valid @RequestBody OffreStage dto){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));}
    @PutMapping("/{id}") public ResponseEntity<OffreStage> update(@PathVariable Long id,@Valid @RequestBody OffreStage dto){return ResponseEntity.ok(service.update(id,dto));}
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}
}
