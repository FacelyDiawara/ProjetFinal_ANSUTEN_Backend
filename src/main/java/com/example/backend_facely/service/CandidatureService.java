package com.example.backend_facely.service;

import com.example.backend_facely.dto.Candidature;
import com.example.backend_facely.entity.Utilisateur;
import com.example.backend_facely.enums.StatutCandidature;
import com.example.backend_facely.enums.StatuOffre;
import com.example.backend_facely.repository.CandidatureRepository;
import com.example.backend_facely.repository.EtudiantRepository;
import com.example.backend_facely.repository.OffreStageRepository;
import com.example.backend_facely.repository.UtilisateurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CandidatureService {
    private final CandidatureRepository repository;
    private final EtudiantRepository etudiantRepository;
    private final OffreStageRepository offreRepository;
    private final UtilisateurRepository utilisateurRepository;

    public CandidatureService(CandidatureRepository repository, EtudiantRepository etudiantRepository, OffreStageRepository offreRepository, UtilisateurRepository utilisateurRepository) { 
        this.repository=repository; 
        this.etudiantRepository=etudiantRepository; 
        this.offreRepository=offreRepository; 
        this.utilisateurRepository=utilisateurRepository;
    }

    public List<Candidature> findAll(StatutCandidature statut, Long etudiantId, Long offreStageId) {
        if (statut != null) return repository.findByStatut(statut).stream().map(this::toDto).toList();
        if (etudiantId != null) return repository.findByEtudiantId(etudiantId).stream().map(this::toDto).toList();
        if (offreStageId != null) return repository.findByOffreStageId(offreStageId).stream().map(this::toDto).toList();
        return repository.findAll().stream().map(this::toDto).toList();
    }
    public Candidature findById(Long id) { return toDto(getEntity(id)); }
    
    public Candidature create(Candidature dto) {
        com.example.backend_facely.entity.Etudiant etudiant = null;
        
        if (dto.getEtudiantId() != null) {
            etudiant = etudiantRepository.findById(dto.getEtudiantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Etudiant introuvable"));
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
            }
            Utilisateur user = utilisateurRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur connecté introuvable"));
            etudiant = etudiantRepository.findByUtilisateurId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun profil étudiant trouvé pour cet utilisateur"));
        }

        if (!etudiant.getUtilisateur().isActif()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Votre compte doit être validé par un administrateur avant de pouvoir candidater.");
        }

        var offre = offreRepository.findById(dto.getOffreStageId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offre de stage introuvable"));
            
        if (offre.getStatut() != StatuOffre.OUVERTE) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette offre est fermée");
        if (repository.existsByEtudiantIdAndOffreStageId(etudiant.getId(), dto.getOffreStageId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Une candidature existe déjà pour cette offre");
        
        var c = new com.example.backend_facely.entity.Candidature(); 
        c.setEtudiant(etudiant); 
        c.setOffreStage(offre); 
        c.setLettreMotivation(dto.getLettreMotivation()); 
        c.setDateSoumission(LocalDateTime.now()); 
        c.setStatut(StatutCandidature.EN_ATTENTE);
        return toDto(repository.save(c));
    }
    
    public Candidature update(Long id,Candidature dto){ var c=getEntity(id); if(dto.getLettreMotivation()!=null) c.setLettreMotivation(dto.getLettreMotivation()); if(dto.getStatut()!=null)c.setStatut(dto.getStatut()); return toDto(repository.save(c)); }
    public void delete(Long id){if(!repository.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Candidature introuvable"); repository.deleteById(id);}
    private com.example.backend_facely.entity.Candidature getEntity(Long id){return repository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Candidature introuvable"));}
    public Candidature toDto(com.example.backend_facely.entity.Candidature c){return Candidature.builder().id(c.getId()).dateSoumission(c.getDateSoumission()).lettreMotivation(c.getLettreMotivation()).statut(c.getStatut()).etudiantId(c.getEtudiant().getId()).offreStageId(c.getOffreStage().getId()).build();}
}
