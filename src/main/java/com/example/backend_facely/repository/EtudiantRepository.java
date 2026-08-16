package com.example.backend_facely.repository;

import com.example.backend_facely.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    Optional<Etudiant> findByMatricule(String matricule);
    Optional<Etudiant> findByUtilisateurId(Long utilisateurId);
    boolean existsByMatricule(String matricule);
}
