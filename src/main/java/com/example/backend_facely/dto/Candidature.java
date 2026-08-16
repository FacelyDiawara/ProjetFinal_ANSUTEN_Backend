package com.example.backend_facely.dto;

import com.example.backend_facely.enums.StatutCandidature;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Candidature {
    private Long id;
    private LocalDateTime dateSoumission;
    @NotBlank private String lettreMotivation;
    private StatutCandidature statut;
    @NotNull private Long etudiantId;
    @NotNull private Long offreStageId;
}
