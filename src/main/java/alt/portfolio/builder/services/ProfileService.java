package alt.portfolio.builder.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.ProfileRepository;
import alt.portfolio.builder.repositories.UserRepository;

// Service pour la gestion des profils
@Service
public class ProfileService {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private UserRepository userRepository;

	// Recuperer tous les profils
	public List<Profile> getAllProfiles() {
		return profileRepository.findAll();
	}

	// Recuperer un profil par son ID
	public Profile getProfileById(UUID id) {
		return profileRepository.findById(id).orElse(null);
	}

	// Recuperer les profils d'un utilisateur
	public List<Profile> getProfilesByUser(UUID userId) {
		return profileRepository.findByOwnerId(userId);
	}

	// Creer un nouveau profil (US-006)
	public Profile createProfile(UUID userId, String name, String description) {
		User owner = userRepository.findById(userId).orElse(null);
		if (owner == null) {
			return null;
		}

		Profile profile = new Profile();
		profile.setName(name);
		profile.setDescription(description);
		profile.setOwner(owner);
		profile.setIsPublishedPortfolio(false);
		profile.setIsPublishedCv(false);

		// Generer le slug unique
		String baseSlug = name.toLowerCase().replaceAll("[àáâãäå]", "a").replaceAll("[èéêë]", "e")
				.replaceAll("[ìíîï]", "i").replaceAll("[òóôõö]", "o").replaceAll("[ùúûü]", "u").replaceAll("[ç]", "c")
				.replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
		String uniqueSlug = baseSlug + "-" + UUID.randomUUID().toString().substring(0, 8);
		profile.setSlug(uniqueSlug);

		return profileRepository.save(profile);
	}

	// Sauvegarder un profil (creation ou mise a jour)
	public Profile saveProfile(Profile profile) {
		return profileRepository.save(profile);
	}

	// Supprimer un profil par son ID
	// La suppression en cascade des rubriques est geree par JPA (CascadeType.ALL +
	// orphanRemoval)
	@Transactional
	public void deleteProfile(UUID id) {
		Profile profile = profileRepository.findById(id).orElse(null);
		if (profile != null) {
			profileRepository.delete(profile);
		}
	}

	// Verifier si un slug existe deja
	public boolean slugExists(String slug) {
		return profileRepository.findBySlug(slug) != null;
	}

	// Recuperer un profil par son slug
	public Profile getProfileBySlug(String slug) {
		return profileRepository.findBySlug(slug);
	}

	// Compter les profils d'un utilisateur
	public long countProfilesByUser(UUID userId) {
		return profileRepository.countByOwnerId(userId);
	}

	// Compter les profils publies d'un utilisateur (Portfolio OU CV)
	public long countPublishedProfilesByUser(UUID userId) {
		List<Profile> profiles = profileRepository.findByOwnerId(userId);
		return profiles.stream().filter(p -> p.isPublished()).count();
	}
}