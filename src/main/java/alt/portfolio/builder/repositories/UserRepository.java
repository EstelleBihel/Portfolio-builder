package alt.portfolio.builder.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.User;

//Repository pour l'acces aux donnees des utilisateurs
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	// Trouver un utilisateur par son nom d'utilisateur
	User findByUsername(String username);

	// Trouver un utilisateur par son email
	User findByEmail(String email);

	// Verifier si un username existe
	boolean existsByUsername(String username);

	// Verifier si un email existe
	boolean existsByEmail(String email);
}