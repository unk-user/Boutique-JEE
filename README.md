# Boutique en ligne (Jakarta EE)

Application web de vente en ligne réalisée dans le cadre du module Architecture JEE (DLA2, ENSA Agadir).
Les visiteurs consultent un catalogue organisé par catégories, les clients inscrits gèrent un panier et passent
des commandes, l'administrateur gère le catalogue, le stock, les commandes et les comptes depuis un back-office.

Le besoin complet est décrit dans le [cahier des charges](docs/cahier-des-charges.md). La conception UML se
trouve dans [`docs/uml/`](docs/uml/) et le rapport dans [`docs/rapport/`](docs/rapport/rapport.md).

## Sommaire

1. [Fonctionnalités](#fonctionnalités)
2. [Technologies](#technologies)
3. [Prérequis](#prérequis)
4. [Installation](#installation)
5. [Configuration de la base de données](#configuration-de-la-base-de-données)
6. [Déploiement](#déploiement)
7. [Comptes de test](#comptes-de-test)
8. [Architecture](#architecture)
9. [Structure du projet](#structure-du-projet)
10. [Documentation](#documentation)

## Fonctionnalités

Tous les besoins du cahier des charges (BF01 à BF23, priorités P1, P2 et P3) sont réalisés.

| Espace | Besoins | URL |
|---|---|---|
| Compte | Inscription (BF01), connexion (BF02), déconnexion (BF03) | `/inscription`, `/connexion`, `/deconnexion` (POST) |
| Compte client | Consultation et modification du profil, changement du mot de passe (BF04) | `/client/profil` |
| Catalogue | Liste paginée (BF05), filtre par catégorie (BF06), recherche par mot-clé (BF07) | `/catalogue` |
| Catalogue | Fiche détaillée d'un produit (BF08) | `/produit?id=N` |
| Panier | Ajout, modification de quantité, retrait, vidage, total (BF09 à BF12) | `/client/panier` |
| Commandes client | Validation du panier (BF13), historique (BF14), détail (BF15), annulation d'une commande « En attente » (BF16) | `/client/commandes` |
| Back-office | Catégories (BF17), produits (BF18), stock (BF19) | `/admin/categories`, `/admin/produits` |
| Back-office | Liste et filtre des commandes (BF20), changement de statut (BF21) | `/admin/commandes` |
| Back-office | Utilisateurs, activation et désactivation des comptes (BF22) | `/admin/utilisateurs` |
| Back-office | Tableau de bord : commandes par statut, chiffre d'affaires, produits en rupture (BF23) | `/admin/tableau-de-bord` |

Les règles de gestion RG01 à RG16 sont contrôlées côté serveur dans la couche service. Le détail figure
dans le [rapport](docs/rapport/rapport.md#5-fonctionnalités-réalisées).

## Technologies

Versions déclarées dans [`pom.xml`](pom.xml) :

| Élément | Version |
|---|---|
| Java | 17 (`maven.compiler.release`) |
| Jakarta Servlet / JSP | 6.0.0 / 3.1.1 (fournis par Tomcat) |
| JSTL | API 3.0.0, implémentation GlassFish 3.0.1 (embarquée dans le war) |
| Hibernate ORM | 6.6.59.Final (annotations JPA, `SessionFactory` native, HQL) |
| MySQL Connector/J | 9.7.0 |
| jBCrypt | 0.4 |
| Bootstrap | 5.3.3 (CDN jsDelivr) |
| Serveur | Apache Tomcat 10.1 |
| Base de données | MySQL 8 |
| Build | Maven, packaging `war` |
| Modélisation | PlantUML |

## Prérequis

- JDK 17 (la variable `JAVA_HOME` doit pointer vers ce JDK)
- Maven 3.9
- Apache Tomcat 10.1 (Jakarta EE 10, espace de noms `jakarta.*` ; Tomcat 9 ne convient pas)
- MySQL 8
- Git

Vérification rapide :

```bash
java -version      # 17.x
mvn -version       # 3.9.x, Java version: 17
mysql --version    # 8.x
```

## Installation

```bash
git clone <url-du-dépôt> Boutique-JEE
cd Boutique-JEE
```

Avant la première compilation, adapter la connexion à la base (section suivante).

## Configuration de la base de données

### 1. Créer la base et l'utilisateur

Se connecter à MySQL en administrateur (`mysql -u root -p`) puis exécuter :

```sql
CREATE DATABASE boutique CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'boutique'@'localhost' IDENTIFIED BY 'boutique';
GRANT ALL PRIVILEGES ON boutique.* TO 'boutique'@'localhost';
FLUSH PRIVILEGES;
```

Le jeu de caractères `utf8mb4` est nécessaire pour les caractères accentués (BNF09).

### 2. Adapter `hibernate.cfg.xml`

Le fichier [`src/main/resources/hibernate.cfg.xml`](src/main/resources/hibernate.cfg.xml) contient les
identifiants de développement local :

```xml
<property name="hibernate.connection.url">jdbc:mysql://127.0.0.1:3307/boutique?characterEncoding=UTF-8</property>
<property name="hibernate.connection.username">boutique</property>
<property name="hibernate.connection.password">boutique</property>
```

> **Important** : le port **3307** correspond à l'environnement de développement (MySQL sous WSL, le port
> 3306 étant occupé par une autre instance). Sur une installation standard, remplacer `3307` par **`3306`**
> et, si besoin, le nom d'utilisateur et le mot de passe par les vôtres.

### 3. Génération des tables

Les tables ne sont pas créées à la main : Hibernate les génère à partir des entités annotées
(`hibernate.hbm2ddl.auto = update`) au déploiement de l'application. On obtient les tables `utilisateur`,
`categorie`, `produit`, `commande` et `ligne_commande`, avec leurs clés étrangères et les contraintes
d'unicité (e-mail, nom de catégorie).

### 4. Créer le premier administrateur

L'inscription publique ne crée que des comptes CLIENT (RG02). Le premier compte ADMIN est inséré par le
script [`docs/sql/admin.sql`](docs/sql/admin.sql), à exécuter **après le premier déploiement** (la table
`utilisateur` doit déjà exister) et **une seule fois** (une seconde exécution échoue sur l'unicité de l'e-mail) :

```bash
mysql -h 127.0.0.1 -P 3306 -u boutique -p boutique < docs/sql/admin.sql
```

(Remplacer `3306` par `3307` dans l'environnement de développement WSL.)

## Déploiement

```bash
mvn clean package
cp target/boutique.war $CATALINA_HOME/webapps/
$CATALINA_HOME/bin/startup.sh
```

L'application est accessible à l'adresse **http://localhost:8080/boutique/** (la page d'accueil redirige vers
le catalogue). Pour redéployer, recopier le nouveau `boutique.war` dans `webapps/` : Tomcat le recharge
automatiquement.

Ordre conseillé pour une première installation : créer la base, adapter `hibernate.cfg.xml`, compiler et
déployer, vérifier que les tables sont créées, puis exécuter `docs/sql/admin.sql`.

> **Fuseau horaire** : les dates de commande et d'inscription sont celles de la JVM (`LocalDateTime.now()`).
> Garder les données de fuseaux horaires (tzdata) du JDK et du système à jour : le Maroc est en UTC+0
> depuis le 20 septembre 2026, et une version ancienne de tzdata affiche une heure décalée.

## Comptes de test

| Rôle | E-mail | Mot de passe |
|---|---|---|
| Administrateur | `admin@boutique.ma` | `Admin@2026` |

Ce compte est créé par `docs/sql/admin.sql` ; le mot de passe est stocké haché avec BCrypt (BNF01). En dehors
d'une démonstration, remplacer ce hachage dans le script par celui d'un autre mot de passe.

Les comptes clients se créent depuis la page **Inscription** (`/inscription`). Le catalogue est vide au départ :
créer quelques catégories et produits depuis le back-office.

## Architecture

Architecture MVC en couches (cahier des charges §6.2) :

```
Navigateur
   │  HTTP
   ▼
Filtres        EncodingFilter (/*), AuthFilter (/client/*), AdminFilter (/admin/*)
               (déclarés dans web.xml, dans cet ordre)
   ▼
Contrôleurs    Servlets @WebServlet : lisent la requête, appellent un service,
   │           puis forward vers une JSP ou redirection après un POST
   ▼
Services       règles de gestion (RG01 à RG16) et transactions Hibernate
   ▼
DAO            GenericDao<T> + requêtes HQL à paramètres nommés
   ▼
Hibernate      SessionFactory unique (HibernateUtil), session liée au thread
   │  JDBC
   ▼
MySQL 8
```

- Les vues sont des JSP placées dans `WEB-INF/views` (inaccessibles directement), écrites uniquement avec
  JSTL et EL (aucun scriptlet), affichage par `<c:out>`.
- `AuthFilter` réserve `/client/*` aux utilisateurs connectés avec le rôle CLIENT (un visiteur est redirigé
  vers `/connexion`, un administrateur vers son tableau de bord) ; `AdminFilter` réserve `/admin/*` au rôle
  ADMIN (visiteur redirigé vers `/connexion`, client refusé avec une erreur 403).
- Le panier est un objet de la `HttpSession`, jamais persisté ; il devient une `Commande` à la validation.
- Le passage de commande s'exécute dans une seule transaction : un stock insuffisant annule toute la commande.

## Structure du projet

```
src/main/java/ma/ensa/boutique/
  model/        Utilisateur, Categorie, Produit, Commande, LigneCommande, Role, StatutCommande,
                Panier, LignePanier (panier en session, non persisté)
  dao/          GenericDao, UtilisateurDao, CategorieDao, ProduitDao, CommandeDao
  service/      AuthService, CatalogueService, PanierService, CommandeService, RegleMetierException
  controller/   InscriptionServlet, ConnexionServlet, DeconnexionServlet, CatalogueServlet, ProduitServlet,
                ProfilServlet, PanierServlet, CommandeServlet, AdminCategorieServlet, AdminProduitServlet,
                AdminCommandeServlet, AdminUtilisateurServlet, TableauDeBordServlet
  filter/       EncodingFilter, AuthFilter, AdminFilter
  util/         HibernateUtil, DemarrageListener, Parametres
src/main/resources/hibernate.cfg.xml
src/main/webapp/
  index.jsp                     redirection vers /catalogue
  WEB-INF/web.xml               session (30 min, cookie), pages d'erreur, encodage des JSP
  WEB-INF/views/client/         catalogue, fiche produit, panier, commandes, profil
  WEB-INF/views/admin/          tableau de bord, catégories, produits, commandes, utilisateurs
  WEB-INF/views/commun/         en-tête, pied de page, connexion, inscription, pagination, erreurs
docs/
  cahier-des-charges.md
  uml/                          sources PlantUML (.puml) et exports SVG
  sql/admin.sql                 premier compte administrateur
  rapport/rapport.md            rapport du projet
```

## Documentation

- [Cahier des charges](docs/cahier-des-charges.md) : besoins (BF), règles de gestion (RG), besoins non
  fonctionnels (BNF).
- [Diagrammes UML](docs/uml/) : cas d'utilisation, classes du domaine, classes de conception, séquences.
  Régénérer les SVG : `plantuml -tsvg -charset UTF-8 docs/uml/*.puml`.
- [Rapport](docs/rapport/rapport.md) : processus (modèle en Y), modèles UML, descriptions textuelles des cas
  d'utilisation, fonctionnalités, choix techniques.
