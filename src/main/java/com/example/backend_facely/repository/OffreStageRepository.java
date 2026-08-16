package com.example.backend_facely.repository;

import com.example.backend_facely.entity.OffreStage;
import com.example.backend_facely.enums.StatuOffre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OffreStageRepository extends JpaRepository<OffreStage, Long> {
    // Query Method 1 : recherche d'offres par secteur d'activite de l'entreprise.
    List<OffreStage> findByEntrepriseSecteurActiviteIgnoreCase(String secteurActivite);

    // Query Method 2 : recherche par statut.
    List<OffreStage> findByStatut(StatuOffre statut);

    // Query Method 3 : recherche textuelle par titre.
    List<OffreStage> findByTitreContainingIgnoreCase(String titre);

    // Query Method 4 : offres d'une entreprise.
    List<OffreStage> findByEntrepriseId(Long entrepriseId);
}
