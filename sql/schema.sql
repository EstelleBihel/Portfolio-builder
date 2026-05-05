-- ============================================
-- Portfolio Builder - Script de création BDD
-- BTS SIO SLAM - Session 2026
-- Auteur : Estelle BIHEL
-- ============================================

-- Création de la base de données
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLE: profile
-- Profils créés par les utilisateurs (CV, Portfolio)
-- ============================================
CREATE TABLE IF NOT EXISTS profile (
    id BINARY(16) NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL COMMENT 'CV ou PORTFOLIO',
    slug VARCHAR(100) DEFAULT NULL,
    is_published BOOLEAN DEFAULT FALSE,
    user_id BINARY(16) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_profile_slug (slug),
    KEY fk_profile_user (user_id),
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) 
        REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLE: rubric
-- Rubriques d'un profil (Expériences, Formations, etc.)
-- ============================================
CREATE TABLE IF NOT EXISTS rubric (
    id BINARY(16) NOT NULL,
    title VARCHAR(100) NOT NULL,
    position INT DEFAULT 0,
    is_visible BOOLEAN DEFAULT TRUE,
    profile_id BINARY(16) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY fk_rubric_profile (profile_id),
    CONSTRAINT fk_rubric_profile FOREIGN KEY (profile_id) 
        REFERENCES profile (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLE: element
-- Éléments de contenu d'une rubrique
-- ============================================
CREATE TABLE IF NOT EXISTS element (
    id BINARY(16) NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    position INT DEFAULT 0,
    rubric_id BINARY(16) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY fk_element_rubric (rubric_id),
    CONSTRAINT fk_element_rubric FOREIGN KEY (rubric_id) 
        REFERENCES rubric (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- DONNÉES DE TEST (optionnel)
-- Décommenter pour insérer des données de démonstration
-- ============================================

-- Insertion d'un utilisateur admin
-- Le mot de passe est hashé en BCrypt (valeur : Admin123!)
-- INSERT INTO users (id, username, email, password, firstname, lastname, role) VALUES (
--     UNHEX(REPLACE(UUID(), '-', '')),
--     'admin',
--     'admin@portfolio.fr',
--     '$2a$10$eTUn1pzh9h.JXahqr/I.Gex2eM85xlkvIxUxE.LPvpsfIVsp45hjG',
--     'Admin',
--     'Système',
--     'ROLE_ADMIN'
-- );

-- Insertion d'un utilisateur standard
-- Le mot de passe est hashé en BCrypt (valeur : User123!)
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
-- INDEX SUPPLÉMENTAIRES (optimisation)
-- ============================================
CREATE INDEX IF NOT EXISTS idx_profile_user ON profile(user_id);
CREATE INDEX IF NOT EXISTS idx_profile_published ON profile(is_published);
CREATE INDEX IF NOT EXISTS idx_rubric_profile ON rubric(profile_id);
CREATE INDEX IF NOT EXISTS idx_rubric_position ON rubric(position);
CREATE INDEX IF NOT EXISTS idx_element_rubric ON element(rubric_id);
CREATE INDEX IF NOT EXISTS idx_element_position ON element(position);

-- ============================================
-- FIN DU SCRIPT
-- ============================================