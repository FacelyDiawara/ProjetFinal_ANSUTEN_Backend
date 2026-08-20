package com.example.backend_facely.service;

import com.example.backend_facely.dto.Entreprise;
import com.example.backend_facely.enums.StatutValidation;
import com.example.backend_facely.entity.Utilisateur;
import com.example.backend_facely.repository.EntrepriseRepository;
import com.example.backend_facely.repository.UtilisateurRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EntrepriseService {

    private final EntrepriseRepository repository;
    private final UtilisateurRepository utilisateurRepository;

    public EntrepriseService(
            EntrepriseRepository repository,
            UtilisateurRepository utilisateurRepository) {

        this.repository = repository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Récupérer toutes les entreprises.
     */
    public List<Entreprise> findAll() {

        return repository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Récupérer une entreprise par son ID.
     */
    public Entreprise findById(Long id) {

        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'identifiant de l'entreprise est obligatoire"
            );
        }

        return toDto(getEntity(id));
    }

    /**
     * Rechercher les entreprises par secteur.
     */
    public List<Entreprise> findBySecteur(String secteur) {

        if (secteur == null || secteur.isBlank()) {
            return findAll();
        }

        return repository
                .findBySecteurActiviteIgnoreCase(secteur)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Créer une entreprise.
     *
     * Le frontend peut envoyer utilisateurId.
     *
     * Si utilisateurId est null, on récupère automatiquement
     * l'utilisateur connecté à partir du JWT.
     */
    public Entreprise create(Entreprise dto) {

        if (dto == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Les données de l'entreprise sont obligatoires"
            );
        }

        System.out.println("=== CREATION ENTREPRISE ===");
        System.out.println("Raison sociale : " + dto.getRaisonSociale());
        System.out.println("Secteur : " + dto.getSecteurActivite());
        System.out.println("Adresse : " + dto.getAdresse());
        System.out.println("Utilisateur ID reçu : " + dto.getUtilisateurId());

        /*
         * 1. Vérifier les informations obligatoires.
         */
        if (dto.getRaisonSociale() == null ||
                dto.getRaisonSociale().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La raison sociale est obligatoire"
            );
        }

        if (dto.getSecteurActivite() == null ||
                dto.getSecteurActivite().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le secteur d'activité est obligatoire"
            );
        }

        if (dto.getAdresse() == null ||
                dto.getAdresse().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'adresse est obligatoire"
            );
        }

        /*
         * 2. Récupérer l'utilisateur.
         *
         * Priorité :
         * - utilisateurId envoyé par Angular
         * - sinon utilisateur connecté via JWT
         */
        Utilisateur user;

        if (dto.getUtilisateurId() != null) {

            System.out.println(
                    "Recherche utilisateur par ID : "
                            + dto.getUtilisateurId()
            );

            user = utilisateurRepository
                    .findById(dto.getUtilisateurId())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Utilisateur introuvable avec l'identifiant : "
                                            + dto.getUtilisateurId()
                            )
                    );

        } else {

            System.out.println(
                    "utilisateurId absent. Recherche de l'utilisateur connecté..."
            );

            Authentication authentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();

            if (authentication == null ||
                    !authentication.isAuthenticated() ||
                    authentication.getName() == null) {

                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Utilisateur non authentifié"
                );
            }

            String email = authentication.getName();

            System.out.println(
                    "Utilisateur connecté : " + email
            );

            user = utilisateurRepository
                    .findByEmail(email)
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Utilisateur connecté introuvable"
                            )
                    );
        }

        /*
         * 3. Vérifier que l'utilisateur ne possède pas
         * déjà un profil entreprise.
         */
        if (repository.findByUtilisateurId(user.getId()).isPresent()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cet utilisateur possède déjà un profil entreprise"
            );
        }

        /*
         * 4. Créer l'entité Entreprise.
         */
        com.example.backend_facely.entity.Entreprise entreprise =
                new com.example.backend_facely.entity.Entreprise();

        entreprise.setRaisonSociale(dto.getRaisonSociale());
        entreprise.setSecteurActivite(dto.getSecteurActivite());
        entreprise.setAdresse(dto.getAdresse());
        entreprise.setSiteWeb(dto.getSiteWeb());

        /*
         * Par défaut une nouvelle entreprise est
         * en attente de validation.
         */
        entreprise.setStatutValidation(
                StatutValidation.EN_ATTENTE
        );

        /*
         * Association avec l'utilisateur.
         */
        entreprise.setUtilisateur(user);

        /*
         * 5. Sauvegarde en base de données.
         */
        var entrepriseSauvegardee =
                repository.save(entreprise);

        System.out.println(
                "Entreprise enregistrée avec ID : "
                        + entrepriseSauvegardee.getId()
        );

        return toDto(entrepriseSauvegardee);
    }

    /**
     * Modifier une entreprise.
     */
    public Entreprise update(
            Long id,
            Entreprise dto) {

        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'identifiant de l'entreprise est obligatoire"
            );
        }

        var entreprise = getEntity(id);

        if (dto.getRaisonSociale() != null &&
                !dto.getRaisonSociale().isBlank()) {

            entreprise.setRaisonSociale(
                    dto.getRaisonSociale()
            );
        }

        if (dto.getSecteurActivite() != null &&
                !dto.getSecteurActivite().isBlank()) {

            entreprise.setSecteurActivite(
                    dto.getSecteurActivite()
            );
        }

        if (dto.getAdresse() != null &&
                !dto.getAdresse().isBlank()) {

            entreprise.setAdresse(
                    dto.getAdresse()
            );
        }

        /*
         * siteWeb est facultatif.
         */
        entreprise.setSiteWeb(dto.getSiteWeb());

        return toDto(repository.save(entreprise));
    }

    /**
     * Modifier le statut de validation.
     */
    public Entreprise updateValidation(
            Long id,
            StatutValidation statut) {

        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'identifiant de l'entreprise est obligatoire"
            );
        }

        if (statut == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le statut de validation est obligatoire"
            );
        }

        var entreprise = getEntity(id);

        entreprise.setStatutValidation(statut);

        return toDto(repository.save(entreprise));
    }

    /**
     * Supprimer une entreprise.
     */
    public void delete(Long id) {

        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'identifiant de l'entreprise est obligatoire"
            );
        }

        if (!repository.existsById(id)) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Entreprise introuvable"
            );
        }

        repository.deleteById(id);
    }

    /**
     * Récupérer l'entité Entreprise.
     */
    public com.example.backend_facely.entity.Entreprise getEntity(
            Long id) {

        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'identifiant de l'entreprise ne peut pas être null"
            );
        }

        return repository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Entreprise introuvable avec l'identifiant : "
                                        + id
                        )
                );
    }

    /**
     * Conversion Entity -> DTO.
     */
    public Entreprise toDto(
            com.example.backend_facely.entity.Entreprise entreprise) {

        Long utilisateurId = null;

        if (entreprise.getUtilisateur() != null) {
            utilisateurId =
                    entreprise.getUtilisateur().getId();
        }

        return Entreprise.builder()
                .id(entreprise.getId())
                .raisonSociale(entreprise.getRaisonSociale())
                .secteurActivite(entreprise.getSecteurActivite())
                .adresse(entreprise.getAdresse())
                .siteWeb(entreprise.getSiteWeb())
                .statutValidation(
                        entreprise.getStatutValidation()
                )
                .utilisateurId(utilisateurId)
                .build();
    }
}