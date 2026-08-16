package com.example.backend_facely.repository;

import com.example.backend_facely.entity.Entreprise;
import com.example.backend_facely.enums.StatutValidation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {
    List<Entreprise> findBySecteurActiviteIgnoreCase(String secteurActivite);
    List<Entreprise> findByStatutValidation(StatutValidation statutValidation);
    Optional<Entreprise> findByUtilisateurId(Long utilisateurId);
}
