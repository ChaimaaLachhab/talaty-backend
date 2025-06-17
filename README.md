# Talaty Backend - API de crédit pour PME

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?style=flat-square&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![MapStruct](https://img.shields.io/badge/MapStruct-1.5.5-red?style=flat-square)
![Maven](https://img.shields.io/badge/Maven-3.9-yellow?style=flat-square&logo=apache-maven)

## 📋 Table des matières

- [Vue d'ensemble](#vue-densemble)
- [Fonctionnalités](#fonctionnalités)
- [Architecture](#architecture)
- [Technologies utilisées](#technologies-utilisées)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Structure du projet](#structure-du-projet)
- [API Endpoints](#api-endpoints)
- [Authentification](#authentification)
- [Documentation API](#documentation-api)
- [Tests](#tests)
- [Déploiement](#déploiement)
- [Contribution](#contribution)

## 🎯 Vue d'ensemble

**Talaty** est une application backend complète développée en **Java 17** avec **Spring Boot 3.2** pour faciliter l'accès au crédit pour les Petites et Moyennes Entreprises (PME) au Maroc. L'application offre un système complet d'eKYC (electronic Know Your Customer), de gestion de documents, et d'évaluation automatique du crédit.

### Objectifs principaux :
- **Simplifier** le processus de demande de crédit pour les PME
- **Automatiser** l'évaluation de crédibilité via un système de scoring intelligent
- **Sécuriser** les données clients avec des protocoles robustes
- **Optimiser** l'expérience utilisateur avec un workflow guidé

## 🚀 Fonctionnalités

### 🔐 Authentification & Sécurité
- **Authentification multi-canal** : Email/Username + Mot de passe OU Code OTP SMS
- **JWT Tokens** sécurisés avec expiration configurable
- **Vérification email** avec tokens temporaires
- **Autorisation basée sur les rôles** (USER, ADMIN)
- **Hashage des mots de passe** avec BCrypt

### 📄 Gestion eKYC (Know Your Customer)
- **Formulaire eKYC complet** : Informations personnelles, professionnelles, bancaires
- **Workflow en étapes** : DRAFT → PENDING → UNDER_REVIEW → APPROVED/REJECTED
- **Calcul automatique** du pourcentage de complétion du profil
- **Prochaines étapes dynamiques** selon la progression utilisateur

### 📁 Gestion de Documents
- **Upload multi-fichiers** vers Cloudinary (PDF, Images)
- **Types de documents** : CIN, Passeport, Registre Commerce, Relevés bancaires
- **Extraction automatique** de données (simulation OCR)
- **Vérification** et validation par les administrateurs
- **Métadonnées complètes** pour chaque document

### ⚖️ Système de Scoring Intelligent
- **Algorithme sur 100 points** basé sur :
    - Informations entreprise (30 points)
    - Données financières (25 points)
    - Documents soumis (25 points)
    - Cohérence des données (20 points)
- **Recalcul automatique** à chaque modification
- **Scoring final** par les administrateurs

### 👨‍💼 Interface Administrateur
- **Dashboard des demandes** en attente
- **Workflow de validation** : Examiner → Approuver/Rejeter
- **Vérification de documents** avec commentaires
- **Historique des décisions** et audit trail

### 🔔 Système de Notifications
- **Notifications en temps réel** pour les utilisateurs
- **Multi-canal** : Base de données + Email
- **Types de notifications** : Soumission, Validation, Rejet
- **Marquage lu/non lu** avec compteurs

### 📧 Services de Communication
- **Envoi d'emails** automatisé (Vérification, OTP, Notifications)
- **Templates HTML** personnalisés et responsive
- **Gestion des échecs** d'envoi avec retry logic

## 🏗️ Architecture

### Pattern d'architecture
- **Architecture en couches** (Controller → Service → Repository → Entity)
- **Mapping automatique** avec MapStruct
- **Réponses standardisées** avec ApiResponse<T>
- **Gestion d'erreurs centralisée** avec @ControllerAdvice

### Sécurité
```
Requête → JWT Filter → Security Config → Controller → Service → Repository → Database
```

### Workflow eKYC
```
Inscription → Vérification Email → Complétion eKYC → Upload Documents → 
Soumission → Review Admin → Décision finale → Notification utilisateur
```

## 🛠️ Technologies utilisées

### Backend Core
- **Java 17** - Version LTS avec performances optimisées
- **Spring Boot 3.2** - Framework principal
- **Spring Security 6** - Authentification et autorisation
- **Spring Data JPA** - Couche de persistance
- **Hibernate** - ORM avec génération automatique des tables

### Base de données
- **MySQL 8.0** - Base de données principale
- **MariaDB Dialect** - Optimisations spécifiques

### Mapping et Validation
- **MapStruct 1.5.5** - Mapping automatique Entity ↔ DTO
- **Hibernate Validator** - Validation des données d'entrée
- **Jakarta Validation** - Annotations de validation

### Sécurité et Tokens
- **JWT (JSON Web Tokens)** - Authentification stateless
- **BCrypt** - Hashage sécurisé des mots de passe
- **UUID** - Génération de tokens uniques

### Services externes
- **Cloudinary** - Stockage et transformation d'images/documents
- **JavaMail** - Envoi d'emails avec templates HTML
- **Simulation SMS** - Service OTP (prêt pour intégration Twilio/AWS SNS)

### Documentation et Tests
- **Swagger/OpenAPI 3** - Documentation API interactive
- **JUnit 5** - Tests unitaires
- **Spring Boot Test** - Tests d'intégration
- **TestContainers** - Tests avec base de données

### Build et Déploiement
- **Maven 3.9** - Gestion des dépendances et build
- **Spring Boot Actuator** - Monitoring et métriques
- **Logback** - Logging configurable

## 📋 Prérequis

### Environnement de développement
- **Java 17** ou supérieur
- **Maven 3.9+**
- **MySQL 8.0** ou **MariaDB 10.6+**
- **IDE** recommandé : IntelliJ IDEA ou Eclipse

### Comptes de service
- **Compte Cloudinary** (gratuit) pour le stockage de fichiers
- **Serveur SMTP** pour l'envoi d'emails (Gmail, SendGrid, etc.)

### Outils optionnels
- **Docker** pour conteneurisation
- **Postman** pour tester les APIs
- **MySQL Workbench** pour la gestion de base de données

## ⚙️ Installation

### 1. Cloner le repository
```bash
git clone https://github.com/your-org/talaty-backend.git
cd talaty-backend
```

### 2. Configuration de la base de données
```sql
-- Créer la base de données
CREATE DATABASE talaty_db1 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Créer un utilisateur dédié (optionnel)
CREATE USER 'talaty_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON talaty_db1.* TO 'talaty_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Installer les dépendances
```bash
mvn clean install
```

### 4. Configuration des variables d'environnement
Créer un fichier `.env` ou configurer les variables système :

```bash
# Base de données
DB_USERNAME=talaty_user
DB_PASSWORD=your_db_password

# Cloudinary
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# JWT
JWT_SECRET=your-super-secret-jwt-key-minimum-256-bits
JWT_EXPIRATION=86400000

# Email SMTP
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Application
FRONTEND_URL=http://localhost:3000
SERVER_PORT=8080
OTP_EXPIRATION=5
OTP_LENGTH=6
EMAIL_VERIFICATION_EXPIRATION=24
MAX_LOGIN_ATTEMPTS=5
LOCKOUT_DURATION=30
```

### 5. Lancer l'application
```bash
# Mode développement
mvn spring-boot:run

# Ou avec le JAR
mvn clean package
java -jar target/talaty-0.0.1-SNAPSHOT.jar
```

### 6. Vérifier l'installation
- **API Health Check** : http://localhost:8080/actuator/health
- **Documentation Swagger** : http://localhost:8080/swagger-ui.html
- **API Docs JSON** : http://localhost:8080/api-docs

## 🔧 Configuration

### Configuration Spring Boot (application.yml)

L'application utilise le profil de configuration suivant pour une flexibilité maximale :

```yaml
spring:
  application:
    name: talaty
  
  # Base de données avec création automatique
  datasource:
    url: jdbc:mysql://localhost:3306/talaty_db1?createDatabaseIfNotExist=true
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
  # JPA/Hibernate avec DDL automatique
  jpa:
    hibernate:
      ddl-auto: update  # Création/mise à jour automatique des tables
    show-sql: true     # Affichage des requêtes SQL (dev uniquement)
  
  # Configuration email SMTP
  mail:
    host: ${MAIL_HOST}
    port: ${MAIL_PORT}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
  
  # CORS pour frontend
  web:
    cors:
      allowed-origins: ${FRONTEND_URL}
      allowed-methods: "GET, POST, PUT, DELETE, OPTIONS"
      allowed-headers: "Authorization, Content-Type"
      allow-credentials: true
  
  # Upload de fichiers (max 10MB)
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

# Configuration JWT sécurisée
security:
  jwt:
    secret-key: ${JWT_SECRET}
    expiration-time: ${JWT_EXPIRATION}

# Configuration OTP
otp:
  expiration-minutes: ${OTP_EXPIRATION}
  length: ${OTP_LENGTH}
```

### Variables d'environnement requises

| Variable | Description | Exemple | Requis |
|----------|-------------|---------|--------|
| `DB_USERNAME` | Utilisateur MySQL | `talaty_user` | ✅ |
| `DB_PASSWORD` | Mot de passe MySQL | `password123` | ✅ |
| `CLOUDINARY_CLOUD_NAME` | Nom du cloud Cloudinary | `my-cloud` | ✅ |
| `CLOUDINARY_API_KEY` | Clé API Cloudinary | `123456789012345` | ✅ |
| `CLOUDINARY_API_SECRET` | Secret API Cloudinary | `abcdef...` | ✅ |
| `JWT_SECRET` | Clé secrète JWT (min 256 bits) | `your-secret-key` | ✅ |
| `JWT_EXPIRATION` | Durée de vie JWT (ms) | `86400000` | ✅ |
| `MAIL_HOST` | Serveur SMTP | `smtp.gmail.com` | ✅ |
| `MAIL_PORT` | Port SMTP | `587` | ✅ |
| `MAIL_USERNAME` | Email d'envoi | `app@company.com` | ✅ |
| `MAIL_PASSWORD` | Mot de passe email | `app-password` | ✅ |
| `FRONTEND_URL` | URL du frontend | `http://localhost:3000` | ✅ |
| `SERVER_PORT` | Port du serveur | `8080` | ❌ |
| `OTP_EXPIRATION` | Durée OTP (minutes) | `5` | ❌ |

## 📁 Structure du projet

```
src/main/java/com/talaty/
├── 📁 config/              # Configurations Spring
│   ├── ApplicationConfiguration.java
│   ├── CloudinaryConfig.java
│   ├── JwtAuthenticationFilter.java
│   ├── SecurityConfiguration.java
│   ├── SwaggerConfig.java
│   └── WebConfig.java
│
├── 📁 controller/          # Contrôleurs REST
│   ├── AdminEKYCController.java
│   ├── AuthenticationController.java
│   ├── DocumentController.java
│   ├── EKYCController.java
│   ├── NotificationController.java
│   └── UserController.java
│
├── 📁 dto/                 # Data Transfer Objects
│   ├── ApiResponse.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── 📁 request/
│   │   ├── AdminRequestDto.java
│   │   ├── CustomerRequestDto.java
│   │   ├── DocumentUploadDto.java
│   │   ├── EKYCRequestDto.java
│   │   ├── OTPRequestDto.java
│   │   └── OTPVerifyDto.java
│   └── 📁 response/
│       ├── AdminResponseDto.java
│       ├── CustomerResponseDto.java
│       ├── DocumentResponseDto.java
│       ├── EKYCResponseDto.java
│       ├── MediaResponseDto.java
│       ├── NotificationResponseDto.java
│       └── OTPResponseDto.java
│
├── 📁 enums/               # Énumérations
│   ├── ApplicationStatus.java
│   ├── DocumentType.java
│   ├── MediaType.java
│   └── Role.java
│
├── 📁 exception/           # Gestion des exceptions
│   ├── GlobalExceptionHandler.java
│   └── FileUploadException.java
│
├── 📁 mapper/              # MapStruct Mappers
│   ├── AdminReviewMapper.java
│   ├── DocumentMapper.java
│   ├── EKYCMapper.java
│   ├── MediaMapper.java
│   ├── NotificationMapper.java
│   ├── OTPMapper.java
│   └── UserMapper.java
│
├── 📁 model/               # Entités JPA
│   ├── Admin.java
│   ├── Customer.java
│   ├── Document.java
│   ├── EKYC.java
│   ├── Media.java
│   ├── Notification.java
│   └── User.java (abstract)
│
├── 📁 repository/          # Repositories Spring Data
│   ├── AdminRepository.java
│   ├── CustomerRepository.java
│   ├── DocumentRepository.java
│   ├── EKYCRepository.java
│   ├── MediaRepository.java
│   ├── NotificationRepository.java
│   └── UserRepository.java
│
├── 📁 service/             # Services métier
│   ├── AdminEKYCService.java
│   ├── AuthenticationService.java
│   ├── DocumentService.java
│   ├── EKYCService.java
│   ├── EmailService.java
│   ├── JwtService.java
│   ├── MediaService.java
│   ├── MediaUploadService.java
│   ├── NotificationService.java
│   ├── OTPService.java
│   ├── ScoringService.java
│   └── UserService.java
│
├── 📁 util/                # Utilitaires
│   ├── FileUploadUtil.java
│   └── UserDetailsServiceImp.java
│
└── TalatyApplication.java  # Classe principale
```

## 🌐 API Endpoints

### 🔐 Authentification (`/api/auth`)

| Méthode | Endpoint | Description | Auth requis |
|---------|----------|-------------|-------------|
| `POST` | `/signup` | Inscription utilisateur | ❌ |
| `POST` | `/login` | Connexion classique | ❌ |
| `POST` | `/add-admin` | Créer administrateur | ADMIN |
| `GET` | `/verify-email` | Vérifier email | ❌ |
| `POST` | `/resend-verification` | Renvoyer email vérification | ❌ |
| `GET` | `/next-steps` | Prochaines étapes utilisateur | USER |

### 📱 OTP (`/api/auth/otp`)

| Méthode | Endpoint | Description | Auth requis |
|---------|----------|-------------|-------------|
| `POST` | `/send` | Envoyer code OTP | ❌ |
| `POST` | `/verify` | Vérifier OTP + Connexion | ❌ |

### 📄 eKYC (`/api/ekyc`)

| Méthode | Endpoint | Description | Auth requis |
|---------|----------|-------------|-------------|
| `POST` | `/` | Créer/Modifier eKYC | USER |
| `GET` | `/me` | Mon eKYC | USER |
| `POST` | `/{id}/submit` | Soumettre eKYC | USER |

### 📁 Documents (`/api/documents`)

| Méthode | Endpoint | Description | Auth requis |
|---------|----------|-------------|-------------|
| `POST` | `/upload` | Upload documents | USER |
| `GET` | `/ekyc/{id}` | Documents d'un eKYC | USER |

### 🔔 Notifications (`/api/notifications`)

| Méthode | Endpoint | Description | Auth requis |
|---------|----------|-------------|-------------|
| `GET` | `/` | Mes notifications | USER |
| `GET` | `/unread` | Notifications non lues | USER |
| `GET` | `/unread/count` | Nombre non lues | USER |
| `PUT` | `/{id}/read` | Marquer comme lue | USER |
| `PUT` | `/mark-all-read` | Tout marquer lu | USER |

### 👨‍💼 Administration (`/api/admin`)

| Méthode | Endpoint | Description | Auth requis |
|---------|----------|-------------|-------------|
| `GET` | `/ekyc/pending` | eKYCs en attente | ADMIN |
| `GET` | `/ekyc/status/{status}` | eKYCs par statut | ADMIN |
| `PUT` | `/ekyc/{id}/review` | Traiter une demande | ADMIN |
| `PUT` | `/document/{id}/verify` | Vérifier document | ADMIN |

## 🔐 Authentification

### JWT Token
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Réponse de connexion
```json
{
  "success": true,
  "message": "Connexion réussie",
  "data": {
    "token": "eyJ...",
    "expirationTime": 86400000,
    "role": "USER",
    "userId": 1,
    "fullName": "Mouhcine TEMSAMANI",
    "verified": false,
    "phoneVerified": true,
    "emailVerified": true,
    "profileCompletion": 75.0,
    "nextSteps": [
      "Informations sur votre entreprise",
      "Téléchargement de documents"
    ]
  }
}
```

### Workflow d'authentification

1. **Inscription** → Vérification email requise
2. **Connexion classique** → Username/Email + Password
3. **Connexion OTP** → Numéro + Code SMS (+ Email si vérifié)
4. **Token JWT** → Valable 24h par défaut
5. **Next Steps** → Guidage selon progression

## 📚 Documentation API

### Swagger UI
Une documentation interactive complète est disponible à :
- **URL** : http://localhost:8080/swagger-ui.html
- **JSON** : http://localhost:8080/api-docs

### Exemples de requêtes

#### Inscription d'un utilisateur
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Mouhcine",
    "lastName": "TEMSAMANI", 
    "username": "mouhcine",
    "email": "mouhcine@example.com",
    "phone": "+212665858585",
    "password": "Password123!"
  }'
```

#### Connexion OTP
```bash
# 1. Demander un code OTP
curl -X POST http://localhost:8080/api/auth/otp/send \
  -H "Content-Type: application/json" \
  -d '{"phone": "+212665858585"}'

# 2. Vérifier le code OTP
curl -X POST http://localhost:8080/api/auth/otp/verify \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "+212665858585",
    "otpCode": "123456"
  }'
```

#### Upload de documents
```bash
curl -X POST http://localhost:8080/api/documents/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "ekycId=1" \
  -F "documentType=COMPANY_REGISTRATION" \
  -F "documentName=Registre de Commerce" \
  -F "files=@document1.pdf" \
  -F "files=@document2.jpg"
```

## 🧪 Tests

### Structure des tests
```
src/test/java/com/talaty/
├── 📁 integration/         # Tests d'intégration
│   ├── AuthenticationFlowTest.java
│   ├── EKYCWorkflowTest.java
│   └── DocumentUploadTest.java
├── 📁 service/             # Tests unitaires services
│   ├── AuthenticationServiceTest.java
│   ├── EKYCServiceTest.java
│   └── ScoringServiceTest.java
└── 📁 controller/          # Tests contrôleurs
    ├── AuthControllerTest.java
    └── EKYCControllerTest.java
```

### Exécuter les tests
```bash
# Tous les tests
mvn test

# Tests spécifiques
mvn test -Dtest=AuthenticationServiceTest
mvn test -Dtest="*Integration*"

# Tests avec couverture
mvn test jacoco:report
```

### Tests avec base de données
Les tests utilisent une base H2 en mémoire pour l'isolation :

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
```

## 🚀 Déploiement

### Build de production
```bash
# Package JAR optimisé
mvn clean package -Pprod

# Skip tests pour déploiement rapide
mvn clean package -DskipTests
```

### Variables d'environnement de production
```bash
# Base de données
DB_USERNAME=talaty_prod
DB_PASSWORD=secure_prod_password

# JWT avec clé forte
JWT_SECRET=your-super-secure-256-bit-production-secret-key
JWT_EXPIRATION=86400000

# Email production
MAIL_HOST=smtp.sendgrid.net
MAIL_USERNAME=apikey
MAIL_PASSWORD=your-sendgrid-api-key

# Cloudinary production
CLOUDINARY_CLOUD_NAME=talaty-prod
# ... autres configs prod
```

### Dockerfile (optionnel)
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY target/talaty-*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Docker Compose
```yaml
version: '3.8'
services:
  talaty-backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_USERNAME=talaty
      - DB_PASSWORD=password
      # ... autres variables
    depends_on:
      - mysql

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: talaty_db1
      MYSQL_USER: talaty
      MYSQL_PASSWORD: password
      MYSQL_ROOT_PASSWORD: rootpass
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

## 🏗️ Architecture de déploiement recommandée

### Environnement de production
```
[Frontend] → [Load Balancer] → [Talaty Backend] → [MySQL Master]
                                      ↓              ↓
                              [Cloudinary]    [MySQL Slave (Read)]
                                      ↓
                              [Email Service]
```

### Monitoring et observabilité
- **Spring Boot Actuator** : Métriques et health checks
- **Logs structurés** : Format JSON pour parsing
- **Prometheus** : Collecte de métriques (optionnel)
- **Grafana** : Dashboards de monitoring (optionnel)

## 🤝 Contribution

### Standards de développement
- **Java 17** avec features modernes
- **Code style** : Google Java Style Guide
- **Tests** : Couverture minimale 80%
- **Documentation** : Javadoc pour méthodes publiques
- **Git** : Conventional Commits

### Workflow de contribution
1. **Fork** le repository
2. **Créer branch** : `feature/nouvelle-fonctionnalite`
3. **Développer** avec tests
4. **Commit** : `feat: ajouter système de scoring avancé`
5. **Pull Request** avec description détaillée

### Structure des commits
```
feat: ajouter authentification OTP
fix: corriger validation email
docs: mettre à jour README API
test: ajouter tests intégration eKYC
refactor: optimiser mappers MapStruct
```

## 📝 Changelog

### Version 1.0.0 (2025-01-17)
- ✅ Authentification JWT + OTP
- ✅ Système eKYC complet
- ✅ Upload documents Cloudinary
- ✅ Scoring automatique
- ✅ Interface admin
- ✅ Notifications multi-canal
- ✅ MapStruct integration
- ✅ Documentation Swagger

## 📞 Support

### Contacts
- **Email** : support@talaty.ma
- **Documentation** : https://docs.talaty.ma
- **Issues** : GitHub Issues

### FAQ

**Q: Comment configurer l'envoi d'emails en local ?**
R: Utiliser un serveur SMTP de test comme MailHog ou un compte Gmail avec app password.

**Q: Puis-je utiliser PostgreSQL au lieu de MySQL ?**
R: Oui, modifier le driver et dialect dans application.yml.

**Q: Comment activer le vrai service SMS pour OTP ?**
R: Intégrer Twilio ou AWS SNS dans OTPService.sendSMS().

**Q: Comment personnaliser l'algorithme de scoring ?**
R: Modifier ScoringService.calculateInitialScore() selon vos critères.

---

## 🎯 Roadmap

### Prochaines fonctionnalités
- [ ] API Analytics et rapports
- [ ] Intégration Twilio pour SMS réels
- [ ] Cache Redis pour performances
- [ ] API Rate Limiting
- [ ] Webhook système pour intégrations externes
- [ ] Support multi-langues (i18n)
- [ ] Audit logs complets

---

**Développé avec ❤️ pour faciliter l'accès au crédit des PME marocaines**

![Talaty](https://img.shields.io/badge/Talaty-Backend-blue?style=for-the-badge)