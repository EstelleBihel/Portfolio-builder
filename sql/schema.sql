-- ============================================
-- Portfolio Builder - Script de creation BDD
-- BTS SIO SLAM - Session 2026
-- Auteur : Estelle BIHEL
-- ============================================
-- Schema complet : 6 entites (User, Profile, Rubric, 
-- Element, Category, Location)
-- ============================================

CREATE DATABASE IF NOT EXISTS portfolio 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE portfolio;

-- ============================================
-- TABLE: users
-- Gestion des utilisateurs de l'application
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BINARY(16) NOT NULL,
    username VARCHAR(45) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL,
    firstname VARCHAR(45) NOT NULL,
    lastname VARCHAR(45) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',
    avatar_url VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLE: category
-- Categories de rubriques (Formation, Experience, etc.)
-- Entite de referentiel : 7 lignes fixes
-- ============================================
CREATE TABLE IF NOT EXISTS category (
    id BINARY(16) NOT NULL,
    name VARCHAR(50) NOT NULL,
    has_dates BIT(1) NOT NULL,
    has_link BIT(1) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertion des 7 categories de reference
INSERT INTO category (id, name, has_dates, has_link) VALUES
(UNHEX(REPLACE(UUID(),'-','')), 'FORMATION',   1, 0),
(UNHEX(REPLACE(UUID(),'-','')), 'EXPERIENCE',  1, 0),
(UNHEX(REPLACE(UUID(),'-','')), 'COMPETENCE',  0, 0),
(UNHEX(REPLACE(UUID(),'-','')), 'PROJET',      1, 1),
(UNHEX(REPLACE(UUID(),'-','')), 'LANGUE',      0, 0),
(UNHEX(REPLACE(UUID(),'-','')), 'LOISIR',      0, 0),
(UNHEX(REPLACE(UUID(),'-','')), 'AUTRE',       0, 0);

-- ============================================
-- TABLE: location
-- Lieux des elements (relation optionnelle 0,1)
-- ============================================
CREATE TABLE IF NOT EXISTS location (
    id BINARY(16) NOT NULL,
    name VARCHAR(120) NOT NULL,
    address VARCHAR(1000) NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLE: profile
-- Profils crees par les utilisateurs (CV et/ou Portfolio)
-- ============================================
CREATE TABLE IF NOT EXISTS profile (
    id BINARY(16) NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT DEFAULT NULL,
    slug VARCHAR(255) DEFAULT NULL,
    is_published_cv BIT(1) NOT NULL DEFAULT b'0',
    is_published_portfolio BIT(1) NOT NULL DEFAULT b'0',
    owner_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_profile_slug (slug),
    KEY idx_profile_owner (owner_id),
    CONSTRAINT fk_profile_owner FOREIGN KEY (owner_id) 
        REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLE: rubric
-- Rubriques d'un profil, categorisees (relation 1,1 avec Category)
-- ============================================
CREATE TABLE IF NOT EXISTS rubric (
    id BINARY(16) NOT NULL,
    name VARCHAR(100) NOT NULL,
    content VARCHAR(5000) DEFAULT NULL,
    display_order INT NOT NULL DEFAULT 0,
    visible BIT(1) NOT NULL DEFAULT b'1',
    profile_id BINARY(16) NOT NULL,
    category_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_rubric_profile (profile_id),
    KEY idx_rubric_category (category_id),
    CONSTRAINT fk_rubric_profile FOREIGN KEY (profile_id) 
        REFERENCES profile (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_rubric_category FOREIGN KEY (category_id) 
        REFERENCES category (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLE: elements
-- Elements de contenu d'une rubrique, avec lieu optionnel
-- Nom de table au PLURIEL (mapping JPA @Table(name="elements"))
-- ============================================
CREATE TABLE IF NOT EXISTS elements (
    id BINARY(16) NOT NULL,
    title VARCHAR(100) NOT NULL,
    subtitle VARCHAR(100) DEFAULT NULL,
    description TEXT DEFAULT NULL,
    start_date VARCHAR(50) DEFAULT NULL,
    end_date VARCHAR(50) DEFAULT NULL,
    link VARCHAR(500) DEFAULT NULL,
    display_order INT NOT NULL DEFAULT 0,
    rubric_id BINARY(16) NOT NULL,
    location_id BINARY(16) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_element_rubric (rubric_id),
    KEY idx_element_location (location_id),
    CONSTRAINT fk_element_rubric FOREIGN KEY (rubric_id) 
        REFERENCES rubric (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_element_location FOREIGN KEY (location_id) 
        REFERENCES location (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- DONNEES DE TEST (optionnel)
-- Decommenter pour inserer des comptes de demonstration
-- ============================================

-- Compte admin (mot de passe BCrypt : Admin123!)
-- INSERT INTO users (id, username, email, password, firstname, lastname, role) VALUES (
--     UNHEX(REPLACE(UUID(), '-', '')),
--     'admin',
--     'admin@portfolio.fr',
--     '$2a$10$eTUn1pzh9h.JXahqr/I.Gex2eM85xlkvIxUxE.LPvpsfIVsp45hjG',
--     'Admin',
--     'Systeme',
--     'ROLE_ADMIN'
-- );

-- Compte utilisateur (mot de passe BCrypt : User123!)
-- INSERT INTO users (id, username, email, password, firstname, lastname, role) VALUES (
--     UNHEX(REPLACE(UUID(), '-', '')),
--     'user',
--     'user@portfolio.fr',
--     '$2a$10$eTUn1pzh9h.JXahqr/I.Gex2eM85xlkvIxUxE.LPvpsfIVsp45hjG',
--     'Utilisateur',
--     'Test',
--     'ROLE_USER'
-- );

-- ============================================
-- FIN DU SCRIPT
-- ============================================