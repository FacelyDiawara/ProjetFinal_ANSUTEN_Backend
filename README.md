# UniStage — Backend Spring Boot + MySQL + JWT

Backend REST de la plateforme UniStage conformément aux étapes 3 et 4 du cahier des charges.

## Technologies
- Java 17
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA / Hibernate
- MySQL
- Spring Security
- JWT avec JJWT
- Bean Validation
- Lombok

## 1. Préparer MySQL

Créer la base (ou laisser `createDatabaseIfNotExist=true` la créer automatiquement) :

```sql
CREATE DATABASE unistage_db;
```

Puis adapter dans `src/main/resources/application.properties` :

```properties
spring.datasource.username=root
spring.datasource.password=
```

## 2. Lancer le backend

Windows PowerShell :

```powershell
.\mvnw.cmd spring-boot:run
```

ou :

```powershell
mvn spring-boot:run
```

API : `http://localhost:8080`

Angular : `http://localhost:4200`

## 3. Authentification JWT

### Inscription

`POST /api/auth/register?role=ETUDIANT`

```json
{
  "nom": "Diallo",
  "prenom": "Facely",
  "email": "facely@example.com",
  "motDePasse": "MotDePasse123"
}
```

Le rôle `ADMIN` ne peut pas être créé par cette route publique.

### Connexion

`POST /api/auth/login`

```json
{
  "email": "facely@example.com",
  "motDePasse": "MotDePasse123"
}
```

Réponse : JWT de type `Bearer`.

Pour les endpoints protégés, Angular/Postman doit envoyer :

```text
Authorization: Bearer <TOKEN>
```

## 4. Rôles

- `ETUDIANT` : consultation et gestion de son parcours/candidatures selon les endpoints exposés.
- `ENTREPRISE` : gestion des offres et consultation/gestion des candidatures.
- `ADMIN` : administration des utilisateurs, entreprises, offres et candidatures.

## 5. Endpoints principaux

### Offres
- `GET /api/offres`
- `GET /api/offres/{id}`
- `POST /api/offres`
- `PUT /api/offres/{id}`
- `DELETE /api/offres/{id}`
- `GET /api/offres?secteur=Informatique`
- `GET /api/offres?statut=OUVERTE`
- `GET /api/offres?titre=Java`

### Candidatures
- `GET /api/candidatures`
- `GET /api/candidatures/{id}`
- `POST /api/candidatures`
- `PUT /api/candidatures/{id}`
- `DELETE /api/candidatures/{id}`
- `GET /api/candidatures?statut=EN_ATTENTE`
- `GET /api/candidatures?etudiantId=1`
- `GET /api/candidatures?offreStageId=1`

### Entreprises
- `GET /api/entreprises`
- `GET /api/entreprises/{id}`
- `POST /api/entreprises`
- `PUT /api/entreprises/{id}`
- `DELETE /api/entreprises/{id}`
- `PUT /api/entreprises/{id}/validation?statut=VALIDEE` (ADMIN)

### Étudiants
- `GET /api/etudiants`
- `GET /api/etudiants/{id}`
- `POST /api/etudiants`
- `PUT /api/etudiants/{id}`
- `DELETE /api/etudiants/{id}`

### Utilisateurs (ADMIN)
- `GET /api/utilisateurs`
- `GET /api/utilisateurs/{id}`
- `PUT /api/utilisateurs/{id}`
- `DELETE /api/utilisateurs/{id}`

## 6. Codes HTTP

- `200 OK` : lecture/modification réussie
- `201 CREATED` : création réussie
- `204 NO CONTENT` : suppression réussie
- `400 BAD REQUEST` : données invalides ou règle métier non respectée
- `401 UNAUTHORIZED` : authentification absente/invalide
- `403 FORBIDDEN` : rôle insuffisant
- `404 NOT FOUND` : ressource inexistante

## 7. Modèle de données

`Utilisateur` est lié en 1-1 à un profil `Etudiant` ou `Entreprise`.

`Entreprise` possède plusieurs `OffreStage`.

`Etudiant` possède plusieurs `Candidature`.

`OffreStage` reçoit plusieurs `Candidature`.

`Candidature` relie donc un `Etudiant` à une `OffreStage`.

## 8. Sécurité

Les mots de passe sont stockés sous forme de hash BCrypt. Le token JWT est vérifié sur les requêtes protégées par `JwtAuthenticationFilter`.

Pour le frontend Angular, ne stocke jamais le mot de passe. Le token doit être supprimé lors de la déconnexion et ne doit plus être envoyé après celle-ci.

## 9. Git

Commits recommandés :

```bash
git add .
git commit -m "feat: implement JPA entities and repositories"
git commit -m "feat: add DTO services and REST CRUD"
git commit -m "feat: add JWT authentication and role security"
git commit -m "feat: configure Angular CORS"
```
