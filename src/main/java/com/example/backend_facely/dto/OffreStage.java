package com.example.backend_facely.dto;

import com.example.backend_facely.enums.StatuOffre;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OffreStage {
    private Long id;
    @NotBlank private String titre;
    @NotBlank private String description;
    @NotBlank private String competencesRequises;
    @NotNull private LocalDate dateDebut;
    @NotNull private LocalDate dateFin;
    @NotBlank private String lieu;
    private StatuOffre statut;
    
    // Rendu optionnel : si null, sera récupéré via l'utilisateur connecté (JWT)
    private Long entrepriseId;
}
