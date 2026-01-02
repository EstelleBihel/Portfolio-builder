package alt.portfolio.builder.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.repositories.ProfileRepository;

/**
 * Service pour la gestion des profils
 */
@Service
public class ProfileService {

	@Autowired
	private ProfileRepository profileRepository;

	/**
	 * Recuperer tous les profils
	 */
	public List<Profile> getAllProfiles() {
		return profileRepository.findAll();
	}

	/**
	 * Recuperer un profil par son ID
	 */
	public Profile getProfileById(UUID id) {
		return profileRepository.findById(id).orElse(null);
	}

	/**
	 * Recuperer les profils d'un utilisateur
	 */
	public List<Profile> getProfilesByUser(UUID userId) {
		return profileRepository.findByOwnerId(userId);
	}

	/**
	 * Sauvegarder un profil (creation ou mise a jour)
	 */
	public Profile saveProfile(Profile profile) {
		return profileRepository.save(profile);
	}

	/**
	 * Supprimer un profil par son ID La suppression en cascade des rubriques est
	 * geree par JPA (CascadeType.ALL + orphanRemoval)
	 */
	@Transactional
	public void deleteProfile(UUID id) {
		Profile profile = profileRepository.findById(id).orElse(null);
		if (profile != null) {
			profileRepository.delete(profile);
		}
	}

	/**
	 * Verifier si un slug existe deja
	 */
	public boolean slugExists(String slug) {
		return profileRepository.findBySlug(slug) != null;
	}

	/**
	 * Recuperer un profil par son slug
	 */
	public Profile getProfileBySlug(String slug) {
		return profileRepository.findBySlug(slug);
	}

	/**
	 * Compter les profils d'un utilisateur
	 */
	public long countProfilesByUser(UUID userId) {
		return profileRepository.countByOwnerId(userId);
	}

	/**
	 * Compter les profils publies d'un utilisateur
	 */
	public long countPublishedProfilesByUser(UUID userId) {
		return profileRepository.countByOwnerIdAndIsPublishedTrue(userId);
	}
}