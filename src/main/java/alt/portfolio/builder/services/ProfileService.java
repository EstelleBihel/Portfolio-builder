package alt.portfolio.builder.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.repositories.ProfileRepository;

@Service
public class ProfileService {

	@Autowired
	private ProfileRepository profileRepository;

	// Récupérer tous les profils (pour la liste)
	public List<Profile> getAllProfiles() {
		return profileRepository.findAll();
	}

	// Récupérer les profils d'un utilisateur spécifique
	public List<Profile> getProfilesByUser(UUID userId) {
		return profileRepository.findByOwner_Id(userId);
	}

	// Récupérer un profil par son ID (pour l'édition)
	public Profile getProfileById(UUID id) {
		return profileRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Profil introuvable avec l'ID : " + id));
	}

	// Sauvegarder ou mettre à jour un profil
	public void saveProfile(Profile profile) {
		profileRepository.save(profile);
	}

	// Supprimer un profil
	public void deleteProfile(UUID id) {
		profileRepository.deleteById(id);
	}
}