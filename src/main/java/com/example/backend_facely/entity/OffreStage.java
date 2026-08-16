package com.example.backend_facely.entity;

import com.example.backend_facely.enums.StatuOffre;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "offres_stage")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OffreStage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titre;

    @Column(nullable = false, length = 3000)
    private String description;

    @Column(nullable = false, length = 1500)
    private String competencesRequises;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @Column(nullable = false, length = 200)
    private String lieu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatuOffre statut = StatuOffre.OUVERTE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;
}
