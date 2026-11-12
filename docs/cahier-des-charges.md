# Cahier des charges : Gestion d'une boutique en ligne

Module Architecture JEE · DLA2 · ENSA Agadir · 9 novembre 2026

Binôme : LAHMOUZ Hamza, ELAOUAD Abdelhamid

## 1. Présentation du projet

Le projet consiste à développer une application web de vente en ligne permettant à des clients d'acheter des produits et à un administrateur de gérer le catalogue et les commandes. Il est réalisé en binôme dans le cadre du module Architecture JEE.

### 1.1 Contexte

Le commerce en ligne est devenu un canal de vente incontournable pour les petites et moyennes boutiques. Une boutique qui gère ses produits et ses commandes manuellement (fichiers Excel, échanges téléphoniques) fait face à des erreurs de stock, à une mauvaise traçabilité des commandes et à une visibilité limitée auprès des clients.

### 1.2 Problématique

Comment offrir à une boutique un outil web simple permettant de publier un catalogue organisé par catégories, de recevoir des commandes en ligne et d'en suivre le traitement, tout en garantissant la cohérence des données (stock, prix, historique) ?

### 1.3 Objectifs

1. Permettre aux visiteurs de consulter le catalogue et de rechercher des produits.
2. Permettre aux clients authentifiés de gérer un panier et de passer des commandes.
3. Permettre à l'administrateur de gérer les catégories, les produits, les commandes et les utilisateurs (CRUD).
4. Concevoir le système avec UML avant son implémentation.
5. Mettre en œuvre une architecture MVC en JEE (Servlets, JSP, JSTL) avec Hibernate pour la persistance.
6. Collaborer via Git et GitHub avec un historique de commits clair.

### 1.4 Périmètre

| Inclus | Hors périmètre |
|---|---|
| Catalogue, catégories, recherche | Paiement en ligne réel (carte bancaire) |
| Authentification et rôles (Client, Administrateur) | Livraison et suivi transporteur |
| Panier en session, passage de commande | Gestion multi-boutiques ou multi-vendeurs |
| Suivi des statuts de commande | Application mobile |
| Back-office d'administration | Envoi d'e-mails transactionnels |

Le paiement est simulé : une commande validée est considérée comme payée à la livraison.

## 2. Acteurs du système

Le système distingue trois acteurs. Le Client hérite des droits du Visiteur (relation de généralisation dans le diagramme de cas d'utilisation).

| Acteur | Description | Accès |
|---|---|---|
| Visiteur | Internaute non authentifié | Consultation du catalogue, recherche, inscription, connexion |
| Client | Utilisateur inscrit et connecté (rôle CLIENT) | Droits du Visiteur + panier, commandes, historique, profil |
| Administrateur | Gestionnaire de la boutique (rôle ADMIN) | Back-office : catégories, produits, commandes, utilisateurs |

## 3. Besoins fonctionnels

Les besoins sont identifiés par un code (BF) et une priorité : P1 = indispensable, P2 = important, P3 = souhaitable. Les besoins P1 constituent le minimum livrable.

### 3.1 Authentification et compte

| Code | Besoin | Acteur | Priorité |
|---|---|---|---|
| BF01 | S'inscrire (nom, prénom, e-mail, mot de passe, adresse, téléphone) | Visiteur | P1 |
| BF02 | Se connecter par e-mail et mot de passe | Visiteur | P1 |
| BF03 | Se déconnecter | Client, Admin | P1 |
| BF04 | Consulter et modifier son profil | Client | P3 |

### 3.2 Catalogue

| Code | Besoin | Acteur | Priorité |
|---|---|---|---|
| BF05 | Consulter la liste des produits disponibles | Visiteur | P1 |
| BF06 | Filtrer les produits par catégorie | Visiteur | P1 |
| BF07 | Rechercher un produit par mot-clé (nom, description) | Visiteur | P2 |
| BF08 | Consulter la fiche détaillée d'un produit (image, prix, stock, description) | Visiteur | P1 |

### 3.3 Panier et commandes (Client)

| Code | Besoin | Acteur | Priorité |
|---|---|---|---|
| BF09 | Ajouter un produit au panier avec une quantité | Client | P1 |
| BF10 | Modifier la quantité d'une ligne du panier | Client | P1 |
| BF11 | Retirer un produit du panier, vider le panier | Client | P1 |
| BF12 | Consulter le panier et son montant total | Client | P1 |
| BF13 | Valider le panier pour passer une commande (adresse de livraison) | Client | P1 |
| BF14 | Consulter l'historique de ses commandes et leur statut | Client | P1 |
| BF15 | Consulter le détail d'une commande | Client | P2 |
| BF16 | Annuler une commande encore « En attente » | Client | P3 |

### 3.4 Administration (back-office)

| Code | Besoin | Acteur | Priorité |
|---|---|---|---|
| BF17 | Créer, consulter, modifier, supprimer une catégorie | Admin | P1 |
| BF18 | Créer, consulter, modifier, supprimer un produit | Admin | P1 |
| BF19 | Gérer le stock d'un produit | Admin | P1 |
| BF20 | Consulter toutes les commandes, filtrer par statut | Admin | P1 |
| BF21 | Changer le statut d'une commande | Admin | P1 |
| BF22 | Consulter la liste des utilisateurs, activer ou désactiver un compte | Admin | P2 |
| BF23 | Consulter un tableau de bord (nombre de commandes, chiffre d'affaires, produits en rupture) | Admin | P3 |

## 4. Règles de gestion

Ces règles sont vérifiées côté serveur (couche service), indépendamment des contrôles effectués dans les formulaires.

| Code | Règle |
|---|---|
| RG01 | L'adresse e-mail d'un utilisateur est unique. |
| RG02 | Un utilisateur possède un seul rôle : CLIENT ou ADMIN. L'inscription publique crée uniquement des comptes CLIENT. |
| RG03 | Un produit appartient à une et une seule catégorie ; une catégorie contient zéro ou plusieurs produits. |
| RG04 | Une catégorie contenant des produits ne peut pas être supprimée. |
| RG05 | Le prix d'un produit est strictement positif ; le stock est un entier supérieur ou égal à 0. |
| RG06 | Un produit dont le stock est à 0 reste visible mais ne peut pas être ajouté au panier. |
| RG07 | La quantité demandée dans le panier ne peut pas dépasser le stock disponible. |
| RG08 | Seul un client authentifié peut ajouter au panier et passer une commande. |
| RG09 | Une commande contient au moins une ligne de commande. |
| RG10 | Le prix unitaire est copié dans la ligne de commande au moment de la commande ; une modification ultérieure du prix du produit n'affecte pas les commandes passées. |
| RG11 | Le montant total d'une commande est la somme (quantité × prix unitaire) de ses lignes. |
| RG12 | La validation d'une commande décrémente le stock de chaque produit commandé dans une même transaction ; si un stock est insuffisant, toute la commande est annulée (rollback). |
| RG13 | Une commande suit le cycle : EN_ATTENTE, puis VALIDEE, EXPEDIEE, LIVREE. Elle peut passer à ANNULEE uniquement depuis EN_ATTENTE ou VALIDEE. |
| RG14 | L'annulation d'une commande réintègre les quantités dans le stock. |
| RG15 | Un produit déjà présent dans une commande n'est pas supprimé physiquement : il est désactivé (champ actif = faux) et disparaît du catalogue. |
| RG16 | Un client ne consulte que ses propres commandes. |

## 5. Besoins non fonctionnels

| Code | Catégorie | Exigence |
|---|---|---|
| BNF01 | Sécurité | Mots de passe stockés hachés (BCrypt), jamais en clair. |
| BNF02 | Sécurité | Accès contrôlé par des filtres : /client/* réservé aux utilisateurs connectés, /admin/* réservé au rôle ADMIN. |
| BNF03 | Sécurité | Requêtes paramétrées (HQL avec paramètres nommés) contre l'injection SQL ; affichage via JSTL c:out contre le XSS. |
| BNF04 | Sécurité | Session invalidée à la déconnexion ; expiration après 30 minutes d'inactivité. |
| BNF05 | Fiabilité | Opérations multi-tables (passage de commande) exécutées dans une transaction Hibernate. |
| BNF06 | Maintenabilité | Architecture MVC en couches (contrôleur, service, DAO, modèle) ; aucune logique métier ni code Java (scriptlets) dans les JSP. |
| BNF07 | Ergonomie | Interface claire et cohérente, messages de confirmation et d'erreur explicites, formulaires validés. |
| BNF08 | Ergonomie | Interface responsive, utilisable sur ordinateur et mobile. |
| BNF09 | Internationalisation | Encodage UTF-8 de bout en bout (base de données, filtre d'encodage, JSP) pour les caractères accentués. |
| BNF10 | Performance | Pagination des listes de produits et de commandes ; temps de réponse inférieur à 2 secondes en local. |
| BNF11 | Portabilité | Déploiement sous forme d'archive .war sur tout conteneur compatible Jakarta EE 10. |
| BNF12 | Traçabilité | Code versionné sur GitHub, une branche par fonctionnalité, fusion via pull request. |

## 6. Contraintes techniques et architecture

Les technologies sont imposées par le module : Servlets et JSP pour la couche web, Hibernate pour la persistance, une base relationnelle et Git/GitHub. Les tables sont générées automatiquement par Hibernate à partir des entités annotées (`hbm2ddl.auto = update`).

### 6.1 Environnement technique

| Élément | Choix |
|---|---|
| Langage | Java 17 |
| Plateforme | Jakarta EE 10 (Servlet 6.0, JSP 3.1, JSTL 3.0, espace de noms jakarta.*) |
| Serveur d'application | Apache Tomcat 10.1 |
| Persistance | Hibernate ORM 6 (annotations JPA, SessionFactory, HQL) |
| Base de données | MySQL 8 |
| Build | Maven (packaging war) |
| Présentation | JSP + JSTL + EL, Bootstrap 5 |
| Sécurité | jBCrypt (hachage des mots de passe), filtres Servlet |
| Versionnement | Git, GitHub (issues, branches, pull requests) |
| Modélisation | UML avec PlantUML |
| IDE | IntelliJ IDEA ou Eclipse |

### 6.2 Architecture logicielle

L'application suit le modèle MVC dans une architecture N-tiers : le navigateur (tiers client), Tomcat qui héberge les couches web et métier, et le serveur MySQL (tiers données).

- **Vue** : pages JSP placées dans `WEB-INF/views` (inaccessibles directement), affichage par JSTL et EL.
- **Contrôleur** : Servlets annotées `@WebServlet` qui lisent la requête, appellent les services et transmettent le résultat à la JSP par `RequestDispatcher`.
- **Service** : logique métier et règles de gestion (stock, statuts, calcul des totaux), gestion des transactions.
- **DAO** : accès aux données via la Session Hibernate (CRUD générique + requêtes HQL spécifiques).
- **Modèle** : entités JPA annotées (`@Entity`, `@OneToMany`, `@ManyToOne`).
- **Transverse** : `HibernateUtil` (SessionFactory unique), filtres d'authentification, d'autorisation et d'encodage.

```
Navigateur (tiers client)
   │ requête HTTP                     ▲ réponse HTML
   ▼                                  │
┌─ Tomcat 10.1 (Jakarta EE 10) : tiers web et métier ─────┐
│  Filtres : Auth, Admin, Encodage                         │
│     ▼                                                    │
│  Contrôleur : Servlets ──forward──► Vue : JSP + JSTL     │
│     ▼                                                    │
│  Service : règles de gestion                             │
│     ▼                                                    │
│  DAO : CRUD et requêtes HQL ··manipule··► Modèle : JPA   │
│     ▼                                                    │
│  Hibernate : Session, transactions                       │
└──────────────────────────────────────────────────────────┘
   │ JDBC
   ▼
MySQL 8 (tiers données)
```

La Servlet orchestre le traitement : elle délègue la logique au service, puis transmet le résultat à la JSP qui produit la réponse HTML.

### 6.3 Organisation du code

```
src/main/java/ma/ensa/boutique/
  model/        Utilisateur, Categorie, Produit, Commande, LigneCommande, Role, StatutCommande
  dao/          GenericDao, UtilisateurDao, CategorieDao, ProduitDao, CommandeDao
  service/      AuthService, CatalogueService, PanierService, CommandeService
  controller/   servlets client et admin
  filter/       AuthFilter, AdminFilter, EncodingFilter
  util/         HibernateUtil
src/main/resources/hibernate.cfg.xml
src/main/webapp/WEB-INF/views/   pages JSP (client, admin, commun)
```

## 7. Modèle de données prévisionnel

Cinq entités persistantes couvrent le périmètre. Le panier n'est pas persisté : il est conservé dans la `HttpSession` du client puis transformé en Commande à la validation. Ce modèle sera détaillé dans le diagramme de classes.

| Entité | Attributs principaux |
|---|---|
| Utilisateur | id, nom, prenom, email (unique), motDePasse (haché), adresse, telephone, role (CLIENT, ADMIN), actif, dateInscription |
| Categorie | id, nom (unique), description |
| Produit | id, nom, description, prix, stock, imageUrl, actif, dateCreation |
| Commande | id, dateCommande, statut (EN_ATTENTE, VALIDEE, EXPEDIEE, LIVREE, ANNULEE), adresseLivraison, montantTotal |
| LigneCommande | id, quantite, prixUnitaire |

| Association | Multiplicité | Mapping Hibernate |
|---|---|---|
| Categorie → Produit | 1 .. 0..* | `@OneToMany(mappedBy)` / `@ManyToOne` + `@JoinColumn(categorie_id)` |
| Utilisateur → Commande | 1 .. 0..* | `@OneToMany(mappedBy)` / `@ManyToOne` + `@JoinColumn(utilisateur_id)` |
| Commande → LigneCommande | 1 .. 1..* (composition) | `@OneToMany(mappedBy, cascade = ALL, orphanRemoval = true)` |
| Produit → LigneCommande | 1 .. 0..* | `@ManyToOne` + `@JoinColumn(produit_id)` |

LigneCommande joue le rôle de table d'association porteuse d'attributs (quantité, prix unitaire) entre Commande et Produit, ce qui remplace un `@ManyToMany` simple.

## 8. Méthodologie, planning et livrables

### 8.1 Méthodologie : modèle en Y

Le projet suit le modèle en Y, un processus itératif piloté par les cas d'utilisation, adapté à une modélisation UML préalable au développement.

| Branche | Activités | Livrables du projet |
|---|---|---|
| Fonctionnelle | Capture des besoins fonctionnels, spécifications, analyse | Ce cahier des charges, diagrammes de cas d'utilisation, descriptions textuelles, diagramme de classes du domaine |
| Technique | Capture des besoins techniques, architecture matérielle et logicielle | Choix technologiques, architecture MVC N-tiers, configuration Hibernate et Tomcat |
| Conception et réalisation | Conception préliminaire et détaillée, codage, tests, déploiement | Diagramme de classes de conception, diagramme(s) de séquence, code source, archive .war |

Les itérations sont courtes (une journée) : chaque itération livre un ensemble de fonctionnalités testées et fusionnées dans la branche main. Le backlog est suivi dans GitHub Issues.

### 8.2 Diagrammes UML prévus

- Diagramme de cas d'utilisation 1 : Visiteur et Client (catalogue, panier, commande, historique, authentification).
- Diagramme de cas d'utilisation 2 : Administrateur (catégories, produits, commandes, utilisateurs).
- Diagramme de classes 1 : modèle du domaine (entités et associations).
- Diagramme de classes 2 : conception MVC (Servlets, services, DAO, entités, HibernateUtil).
- Diagramme de séquence 1 : « Passer une commande ».
- Diagramme de séquence 2 : « S'authentifier ».

### 8.3 Planning

1. **Itération 1** : cahier des charges, cas d'utilisation, diagramme de classes du domaine ; initialisation du dépôt, projet Maven, entités, génération des tables.
2. **Itération 2** : authentification, filtres, DAO, CRUD catégories et produits, affichage du catalogue.
3. **Itération 3** : panier, passage de commande (transaction et stock), historique, gestion des statuts.
4. **Itération 4** : validations, pages d'erreur, interface, diagrammes de conception et de séquence, tests manuels.
5. **Itération 5** : rédaction du rapport, README, préparation de la soutenance.

### 8.4 Répartition du binôme

| Membre | Développement | Conception et documentation |
|---|---|---|
| LAHMOUZ Hamza | Initialisation Maven et Hibernate, entités, back-office administrateur, filtres | Diagrammes de classes, diagramme de séquence « Passer une commande », architecture technique |
| ELAOUAD Abdelhamid | Authentification, catalogue, panier, passage de commande, historique | Cas d'utilisation et descriptions textuelles, diagramme de séquence « S'authentifier », captures d'écran |

Chaque membre développe sur une branche `feature/*` et la fusion dans main se fait par pull request relue par l'autre membre.

### 8.5 Livrables

- Code source sur GitHub avec un historique de commits clair et organisé, et un README (installation, configuration de la base, déploiement).
- Rapport PDF : processus de développement, modèles UML, fonctionnalités réalisées, choix techniques, difficultés rencontrées et solutions adoptées.
