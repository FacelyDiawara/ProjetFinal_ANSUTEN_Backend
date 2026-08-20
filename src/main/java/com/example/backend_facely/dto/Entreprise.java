package com.example.backend_facely.dto;

import com.example.backend_facely.enums.StatutValidation;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entreprise {

    private Long id;

    @NotBlank(message = "La raison sociale est obligatoire")
    private String raisonSociale;

    @NotBlank(message = "Le secteur d'activité est obligatoire")
    private String secteurActivite;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    private String siteWeb;

    private StatutValidation statutValidation;

    /*
     * Facultatif lors de la création.
     *
     * Si absent, le backend utilise automatiquement
     * l'utilisateur connecté via le JWT.
     */
    private Long utilisateurId;
}