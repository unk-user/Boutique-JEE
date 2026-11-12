-- Création du premier compte administrateur (RG02 : l'inscription publique ne crée que des comptes CLIENT).
-- À exécuter une fois, après le premier déploiement (les tables sont créées par Hibernate) :
--   mysql -h 127.0.0.1 -P 3306 -u boutique -p boutique < docs/sql/admin.sql
-- Le port 3306 est celui d'une installation standard de MySQL ; dans l'environnement de développement
-- WSL, MySQL écoute sur le port 3307 (voir hibernate.cfg.xml) : remplacer alors -P 3306 par -P 3307.
-- Identifiants : admin@boutique.ma / Admin@2026 (à changer en production).
-- Le mot de passe est stocké haché avec BCrypt (BNF01).

INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, adresse, telephone, role, actif, date_inscription)
VALUES ('Administrateur', 'Boutique', 'admin@boutique.ma',
        '$2a$10$OhjWvl.7rm2.wzR5COF5IOoAPDHnYkqNJezv5/JN5nSwyKosfg6wq',
        NULL, NULL, 'ADMIN', TRUE, NOW());
