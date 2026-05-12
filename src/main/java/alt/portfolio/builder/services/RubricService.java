package alt.portfolio.builder.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.Rubric;
import alt.portfolio.builder.repositories.ProfileRepository;
import alt.portfolio.builder.repositories.RubricRepository;

//Service pour la gestion des rubriques
@Service
public class RubricService {

	@Autowired
	private RubricRepository rubricRepository;

	@Autowired
	private ProfileRepository profileRepository;

	// Recuperer toutes les rubriques d'un profil
	public List<Rubric> getRubricsByProfile(UUID profileId) {
		return rubricRepository.findByProfileIdOrderByDisplayOrderAsc(profileId);
	}

	// Recuperer les rubriques visibles d'un profil
	public List<Rubric> getVisibleRubricsByProfile(UUID profileId) {
		return rubricRepository.findByProfileIdAndVisibleTrueOrderByDisplayOrderAsc(profileId);
	}

	// Recuperer une rubrique par son ID
	public Rubric getRubricById(UUID id) {
		return rubricRepository.findById(id).orElse(null);
	}

	// Creer une nouvelle rubrique
	@Transactional
	public Rubric createRubric(UUID profileId, String name) {
		return createRubric(profileId, name, "AUTRE", null);
	}

	// Creer une nouvelle rubrique avec type
	@Transactional
	public Rubric createRubric(UUID profileId, String name, String type, String content) {
		Profile profile = profileRepository.findById(profileId)
				.orElseThrow(() -> new IllegalArgumentException("Profil introuvable"));

		// Determiner l'ordre d'affichage (dernier + 1)
		Rubric lastRubric = rubricRepository.findFirstByProfileIdOrderByDisplayOrderDesc(profileId);
		int newOrder = 0;
		if (lastRubric != null && lastRubric.getDisplayOrder() != null) {
			newOrder = lastRubric.getDisplayOrder() + 1;
		}

		Rubric rubric = new Rubric();
		rubric.setName(name);
		rubric.setType(type);
		rubric.setContent(content);
		rubric.setDisplayOrder(newOrder);
		rubric.setVisible(true);
		rubric.setProfile(profile);

		return rubricRepository.save(rubric);
	}

	// Sauvegarder une rubrique (creation ou mise a jour)
	public Rubric saveRubric(Rubric rubric) {
		return rubricRepository.save(rubric);
	}

	// Supprimer une rubrique
	@Transactional
	public void deleteRubric(UUID id) {
		rubricRepository.deleteById(id);
	}

	// Compter les rubriques d'un profil
	public long countRubricsByProfile(UUID profileId) {
		return rubricRepository.countByProfileId(profileId);
	}

	// Changer la visibilite d'une rubrique
	@Transactional
	public void toggleVisibility(UUID id) {
		Rubric rubric = rubricRepository.findById(id).orElse(null);
		if (rubric != null) {
			rubric.setVisible(!rubric.getVisible());
			rubricRepository.save(rubric);
		}
	}

	// Deplacer une rubrique vers le haut
	@Transactional
	public void moveUp(UUID id) {
		Rubric rubric = rubricRepository.findById(id).orElse(null);
		if (rubric != null && rubric.getDisplayOrder() > 0) {
			List<Rubric> rubrics = rubricRepository.findByProfileIdOrderByDisplayOrderAsc(rubric.getProfile().getId());
			int currentIndex = rubrics.indexOf(rubric);
			if (currentIndex > 0) {
				Rubric previous = rubrics.get(currentIndex - 1);
				int tempOrder = rubric.getDisplayOrder();
				rubric.setDisplayOrder(previous.getDisplayOrder());
				previous.setDisplayOrder(tempOrder);
				rubricRepository.save(rubric);
				rubricRepository.save(previous);
			}
		}
	}

	// Deplacer une rubrique vers le bas
	@Transactional
	public void moveDown(UUID id) {
		Rubric rubric = rubricRepository.findById(id).orElse(null);
		if (rubric != null) {
			List<Rubric> rubrics = rubricRepository.findByProfileIdOrderByDisplayOrderAsc(rubric.getProfile().getId());
			int currentIndex = rubrics.indexOf(rubric);
			if (currentIndex < rubrics.size() - 1) {
				Rubric next = rubrics.get(currentIndex + 1);
				int tempOrder = rubric.getDisplayOrder();
				rubric.setDisplayOrder(next.getDisplayOrder());
				next.setDisplayOrder(tempOrder);
				rubricRepository.save(rubric);
				rubricRepository.save(next);
			}
		}
	}
}