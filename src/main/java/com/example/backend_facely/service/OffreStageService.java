package com.example.backend_facely.service;

import com.example.backend_facely.dto.OffreStage;
import com.example.backend_facely.entity.Utilisateur;
import com.example.backend_facely.enums.StatuOffre;
import com.example.backend_facely.enums.StatutValidation;
import com.example.backend_facely.repository.EntrepriseRepository;
import com.example.backend_facely.repository.OffreStageRepository;
import com.example.backend_facely.repository.UtilisateurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OffreStageService {
    private final OffreStageRepository repository;
    private final EntrepriseRepository entrepriseRepository;
    private final UtilisateurRepository utilisateurRepository;

    public OffreStageService(OffreStageRepository repository, EntrepriseRepository entrepriseRepository, UtilisateurRepository utilisateurRepository) { 
        this.repository = repository; 
        this.entrepriseRepository = entrepriseRepository; 
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<OffreStage> findAll(String secteur, String titre, StatuOffre statut) {
        if (secteur != null && !secteur.isBlank()) return repository.findByEntrepriseSecteurActiviteIgnoreCase(secteur).stream().map(this::toDto).toList();
        if (titre != null && !titre.isBlank()) return repository.findByTitreContainingIgnoreCase(titre).stream().map(this::toDto).toList();
        if (statut != null) return repository.findByStatut(statut).stream().map(this::toDto).toList();
        return repository.findAll().stream().map(this::toDto).toList();
    }
    public OffreStage findById(Long id) { return toDto(getEntity(id)); }

    public OffreStage create(OffreStage dto) {
        validateDates(dto);
        
        com.example.backend_facely.entity.Entreprise entreprise = null;
        
        if (dto.getEntrepriseId() != null) {
            entreprise = entrepriseRepository.findById(dto.getEntrepriseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entreprise introuvable"));
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
            }
            Utilisateur user = utilisateurRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur connecté introuvable"));
            entreprise = entrepriseRepository.findByUtilisateurId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun profil entreprise trouvé pour cet utilisateur"));
        }

        if (!entreprise.getUtilisateur().isActif()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le compte entreprise doit être activé par un administrateur.");
        }

        if (entreprise.getStatutValidation() != StatutValidation.VALIDEE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'entreprise doit être validée par un administrateur avant de publier une offre.");
        }

        var e = new com.example.backend_facely.entity.OffreStage();
        e.setTitre(dto.getTitre()); e.setDescription(dto.getDescription()); e.setCompetencesRequises(dto.getCompetencesRequises()); e.setDateDebut(dto.getDateDebut()); e.setDateFin(dto.getDateFin()); e.setLieu(dto.getLieu()); e.setStatut(dto.getStatut() == null ? StatuOffre.OUVERTE : dto.getStatut()); e.setEntreprise(entreprise);
        return toDto(repository.save(e));
    }

    public OffreStage update(Long id, OffreStage dto) {
        validateDates(dto); var e = getEntity(id); e.setTitre(dto.getTitre()); e.setDescription(dto.getDescription()); e.setCompetencesRequises(dto.getCompetencesRequises()); e.setDateDebut(dto.getDateDebut()); e.setDateFin(dto.getDateFin()); e.setLieu(dto.getLieu()); if (dto.getStatut()!=null) e.setStatut(dto.getStatut()); return toDto(repository.save(e));
    }
    public void delete(Long id) { if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Offre de stage introuvable"); repository.deleteById(id); }
    public com.example.backend_facely.entity.OffreStage getEntity(Long id) { return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offre de stage introuvable")); }
    private void validateDates(OffreStage dto) { if (dto.getDateDebut()!=null && dto.getDateFin()!=null && dto.getDateFin().isBefore(dto.getDateDebut())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date de fin doit être postérieure ou égale à la date de début"); }
    public OffreStage toDto(com.example.backend_facely.entity.OffreStage e) { return OffreStage.builder().id(e.getId()).titre(e.getTitre()).description(e.getDescription()).competencesRequises(e.getCompetencesRequises()).dateDebut(e.getDateDebut()).dateFin(e.getDateFin()).lieu(e.getLieu()).statut(e.getStatut()).entrepriseId(e.getEntreprise().getId()).build(); }
}
