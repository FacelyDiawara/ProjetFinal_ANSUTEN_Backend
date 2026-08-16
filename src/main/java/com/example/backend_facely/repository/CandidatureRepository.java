package com.example.backend_facely.repository;

import com.example.backend_facely.entity.Candidature;
import com.example.backend_facely.enums.StatutCandidature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    // Query Method demandé : candidatures par statut.
    List<Candidature> findByStatut(StatutCandidature statut);

    List<Candidature> findByEtudiantId(Long etudiantId);
    List<Candidature> findByOffreStageId(Long offreStageId);
    boolean existsByEtudiantIdAndOffreStageId(Long etudiantId, Long offreStageId);
}
