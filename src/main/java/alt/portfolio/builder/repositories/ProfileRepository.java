package alt.portfolio.builder.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Profile;

// Repository pour l'acces aux donnees des profils
@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

	// Trouver tous les profils d'un utilisateur
	List<Profile> findByOwnerId(UUID ownerId);

	// Trouver un profil par son slug
	Profile findBySlug(String slug);

	// Compter les profils d'un utilisateur
	long countByOwnerId(UUID ownerId);

	// Trouver les profils avec Portfolio publie
	List<Profile> findByOwnerIdAndIsPublishedPortfolioTrue(UUID ownerId);

	// Trouver les profils avec CV publie
	List<Profile> findByOwnerIdAndIsPublishedCvTrue(UUID ownerId);
}