package com.example.backend_facely.service;

import com.example.backend_facely.dto.Etudiant;
import com.example.backend_facely.entity.Utilisateur;
import com.example.backend_facely.repository.EtudiantRepository;
import com.example.backend_facely.repository.UtilisateurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EtudiantService {
    private final EtudiantRepository repository;
    private final UtilisateurRepository utilisateurRepository;

    public EtudiantService(EtudiantRepository repository, UtilisateurRepository utilisateurRepository) {
        this.repository = repository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<Etudiant> findAll() { return repository.findAll().stream().map(this::toDto).toList(); }
    public Etudiant findById(Long id) { return toDto(getEntity(id)); }

    public Etudiant create(Etudiant dto) {
        if (repository.existsByMatricule(dto.getMatricule())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matricule déjà utilisé");
        
        Utilisateur user;
        if (dto.getUtilisateurId() != null) {
            user = utilisateurRepository.findById(dto.getUtilisateurId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
            }
            user = utilisateurRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur connecté introuvable"));
        }

        if (repository.findByUtilisateurId(user.getId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cet utilisateur possède déjà un profil étudiant");
        }
        
        var e = new com.example.backend_facely.entity.Etudiant();
        e.setMatricule(dto.getMatricule()); e.setFiliere(dto.getFiliere()); e.setNiveau(dto.getNiveau()); e.setTelephone(dto.getTelephone()); e.setCv(dto.getCv()); e.setUtilisateur(user);
        return toDto(repository.save(e));
    }

    public Etudiant update(Long id, Etudiant dto) {
        var e = getEntity(id);
        if (dto.getMatricule() != null && !dto.getMatricule().equalsIgnoreCase(e.getMatricule())) {
            repository.findByMatricule(dto.getMatricule()).ifPresent(other -> { if (!other.getId().equals(id)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matricule déjà utilisé"); });
            e.setMatricule(dto.getMatricule());
        }
        e.setFiliere(dto.getFiliere()); e.setNiveau(dto.getNiveau()); e.setTelephone(dto.getTelephone()); e.setCv(dto.getCv());
        return toDto(repository.save(e));
    }

    public void delete(Long id) { if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Etudiant introuvable"); repository.deleteById(id); }
    public com.example.backend_facely.entity.Etudiant getEntity(Long id) { return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Etudiant introuvable")); }
    public Etudiant toDto(com.example.backend_facely.entity.Etudiant e) { return Etudiant.builder().id(e.getId()).matricule(e.getMatricule()).filiere(e.getFiliere()).niveau(e.getNiveau()).telephone(e.getTelephone()).cv(e.getCv()).utilisateurId(e.getUtilisateur().getId()).build(); }
}
