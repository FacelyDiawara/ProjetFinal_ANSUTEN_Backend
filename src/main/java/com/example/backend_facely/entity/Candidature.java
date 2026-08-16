package com.example.backend_facely.entity;

import com.example.backend_facely.enums.StatutCandidature;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "candidatures", uniqueConstraints = @UniqueConstraint(columnNames = {"etudiant_id", "offre_stage_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Candidature {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dateSoumission = LocalDateTime.now();

    @Column(nullable = false, length = 3000)
    private String lettreMotivation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatutCandidature statut = StatutCandidature.EN_ATTENTE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offre_stage_id", nullable = false)
    private OffreStage offreStage;
}
