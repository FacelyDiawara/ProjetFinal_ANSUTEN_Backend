package com.example.backend_facely.dto;

import com.example.backend_facely.enums.Role;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Utilisateur {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private boolean actif;
}
