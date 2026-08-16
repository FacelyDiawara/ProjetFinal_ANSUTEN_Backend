package com.example.backend_facely.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Etudiant {
    private Long id;
    @NotBlank private String matricule;
    @NotBlank private String filiere;
    @NotBlank private String niveau;
    private String telephone;
    private String cv;
    private Long utilisateurId;
}
