package com.example.backend_facely.service;

import com.example.backend_facely.dto.Entreprise;
import com.example.backend_facely.enums.StatutValidation;
import com.example.backend_facely.repository.EntrepriseRepository;
import com.example.backend_facely.repository.UtilisateurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EntrepriseService {
    private final EntrepriseRepository repository;
    private final UtilisateurRepository utilisateurRepository;

    public EntrepriseService(EntrepriseRepository repository, UtilisateurRepository utilisateurRepository) { this.repository = repository; this.utilisateurRepository = utilisateurRepository; }
    public List<Entreprise> findAll() { return repository.findAll().stream().map(this::toDto).toList(); }
    public Entreprise findById(Long id) { return toDto(getEntity(id)); }
    public List<Entreprise> findBySecteur(String secteur) { return repository.findBySecteurActiviteIgnoreCase(secteur).stream().map(this::toDto).toList(); }

    public Entreprise create(Entreprise dto) {
        var user = utilisateurRepository.findById(dto.getUtilisateurId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        if (repository.findByUtilisateurId(user.getId()).isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cet utilisateur possède déjà un profil entreprise");
        var e = new com.example.backend_facely.entity.Entreprise();
        e.setRaisonSociale(dto.getRaisonSociale()); e.setSecteurActivite(dto.getSecteurActivite()); e.setAdresse(dto.getAdresse()); e.setSiteWeb(dto.getSiteWeb()); e.setStatutValidation(StatutValidation.EN_ATTENTE); e.setUtilisateur(user);
        return toDto(repository.save(e));
    }

    public Entreprise update(Long id, Entreprise dto) {
        var e = getEntity(id); e.setRaisonSociale(dto.getRaisonSociale()); e.setSecteurActivite(dto.getSecteurActivite()); e.setAdresse(dto.getAdresse()); e.setSiteWeb(dto.getSiteWeb());
        return toDto(repository.save(e));
    }

    public Entreprise updateValidation(Long id, StatutValidation statut) { var e=getEntity(id); e.setStatutValidation(statut); return toDto(repository.save(e)); }
    public void delete(Long id) { if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entreprise introuvable"); repository.deleteById(id); }
    public com.example.backend_facely.entity.Entreprise getEntity(Long id) { return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entreprise introuvable")); }
    public Entreprise toDto(com.example.backend_facely.entity.Entreprise e) { return Entreprise.builder().id(e.getId()).raisonSociale(e.getRaisonSociale()).secteurActivite(e.getSecteurActivite()).adresse(e.getAdresse()).siteWeb(e.getSiteWeb()).statutValidation(e.getStatutValidation()).utilisateurId(e.getUtilisateur().getId()).build(); }
}
