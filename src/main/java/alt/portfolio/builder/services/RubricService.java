package alt.portfolio.builder.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.Rubric;
import alt.portfolio.builder.repositories.ProfileRepository;
import alt.portfolio.builder.repositories.RubricRepository;

@Service
public class RubricService {

	@Autowired
	private RubricRepository rubricRepository;

	@Autowired
	private ProfileRepository profileRepository;

	public void createRubric(UUID profileId, String name) {
		Profile profile = profileRepository.findById(profileId)
				.orElseThrow(() -> new RuntimeException("Profil non trouvé"));

		Rubric rubric = new Rubric();
		rubric.setName(name);
		rubric.setDisplayOrder(0); // Correction ici (était setOrder)
		rubric.setProfile(profile);

		rubricRepository.save(rubric);
	}

	public Rubric getRubricById(UUID id) {
		return rubricRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Rubrique introuvable avec l'ID : " + id));
	}

	// AJOUT DE LA MÉTHODE MANQUANTE
	public void saveRubric(Rubric rubric) {
		rubricRepository.save(rubric);
	}
}