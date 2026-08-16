package com.example.backend_facely.controller;

import com.example.backend_facely.dto.Etudiant;
import com.example.backend_facely.service.EtudiantService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {
    private final EtudiantService service;
    public EtudiantController(EtudiantService service){this.service=service;}
    @GetMapping public ResponseEntity<List<Etudiant>> getAll(){return ResponseEntity.ok(service.findAll());}
    @GetMapping("/{id}") public ResponseEntity<Etudiant> getById(@PathVariable Long id){return ResponseEntity.ok(service.findById(id));}
    @PostMapping public ResponseEntity<Etudiant> create(@Valid @RequestBody Etudiant dto){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));}
    @PutMapping("/{id}") public ResponseEntity<Etudiant> update(@PathVariable Long id,@Valid @RequestBody Etudiant dto){return ResponseEntity.ok(service.update(id,dto));}
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}
}
