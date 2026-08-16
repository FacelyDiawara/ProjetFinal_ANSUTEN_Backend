package com.example.backend_facely.entity;

import com.example.backend_facely.enums.StatutValidation;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "entreprises")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Entreprise {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String raisonSociale;

    @Column(nullable = false, length = 120)
    private String secteurActivite;

    @Column(nullable = false, length = 250)
    private String adresse;

    @Column(length = 200)
    private String siteWeb;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatutValidation statutValidation = StatutValidation.EN_ATTENTE;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    private Utilisateur utilisateur;
}
