package com.example.backend_facely.dto;

import com.example.backend_facely.enums.StatutValidation;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Entreprise {
    private Long id;
    @NotBlank private String raisonSociale;
    @NotBlank private String secteurActivite;
    @NotBlank private String adresse;
    private String siteWeb;
    private StatutValidation statutValidation;
    private Long utilisateurId;
}
