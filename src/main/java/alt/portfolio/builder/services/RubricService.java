package alt.portfolio.builder.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import alt.portfolio.builder.entities.Category;
import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.Rubric;
import alt.portfolio.builder.repositories.CategoryRepository;
import alt.portfolio.builder.repositories.ProfileRepository;
import alt.portfolio.builder.repositories.RubricRepository;

// Service pour la gestion des rubriques
@Service
public class RubricService {

	@Autowired
	private RubricRepository rubricRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private CategoryRepository categoryRepository;

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

	// Creer une nouvelle rubrique (categorie par defaut : AUTRE)
	@Transactional
	public Rubric createRubric(UUID profileId, String name) {
		Category autre = categoryRepository.findByName("AUTRE");
		return createRubricWithCategory(profileId, name, autre.getId(), null);
	}

	// Creer une nouvelle rubrique en resolvant la categorie par son nom
	@Transactional
	public Rubric createRubric(UUID profileId, String name, String categoryName, String content) {
		Category category = resolveCategory(categoryName);
		return createRubricWithCategory(profileId, name, category.getId(), content);
	}

	// Sauvegarder une rubrique (creation ou mise a jour)
	public Rubric saveRubric(Rubric rubric) {
		return rubricRepository.save(rubric);
	}

	// Resout la categorie correspondant a un nom (fallback sur AUTRE)
	private Category resolveCategory(String categoryName) {
		Category category = null;
		if (categoryName != null && !categoryName.trim().isEmpty()) {
			category = categoryRepository.findByName(categoryName.trim());
		}
		if (category == null) {
			category = categoryRepository.findByName("AUTRE");
		}
		return category;
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
		if (rubric == null) {
			return;
		}
		List<Rubric> rubrics = rubricRepository.findByProfileIdOrderByDisplayOrderAsc(rubric.getProfile().getId());

		int currentIndex = -1;
		for (int i = 0; i < rubrics.size(); i++) {
			if (rubrics.get(i).getId().equals(id)) {
				currentIndex = i;
				break;
			}
		}

		if (currentIndex > 0) {
			Rubric previous = rubrics.get(currentIndex - 1);
			Integer tempOrder = rubric.getDisplayOrder();
			rubric.setDisplayOrder(previous.getDisplayOrder());
			previous.setDisplayOrder(tempOrder);
			rubricRepository.save(rubric);
			rubricRepository.save(previous);
		}
	}

	// Deplacer une rubrique vers le bas
	@Transactional
	public void moveDown(UUID id) {
		Rubric rubric = rubricRepository.findById(id).orElse(null);
		if (rubric == null) {
			return;
		}
		List<Rubric> rubrics = rubricRepository.findByProfileIdOrderByDisplayOrderAsc(rubric.getProfile().getId());

		int currentIndex = -1;
		for (int i = 0; i < rubrics.size(); i++) {
			if (rubrics.get(i).getId().equals(id)) {
				currentIndex = i;
				break;
			}
		}

		if (currentIndex >= 0 && currentIndex < rubrics.size() - 1) {
			Rubric next = rubrics.get(currentIndex + 1);
			Integer tempOrder = rubric.getDisplayOrder();
			rubric.setDisplayOrder(next.getDisplayOrder());
			next.setDisplayOrder(tempOrder);
			rubricRepository.save(rubric);
			rubricRepository.save(next);
		}
	}

	// Lister toutes les categories (pour les dropdowns de formulaire)
	public List<Category> getAllCategories() {
		return categoryRepository.findAllByOrderByNameAsc();
	}

	// Creer une rubrique en choisissant directement la categorie (par son id)
	@Transactional
	public Rubric createRubricWithCategory(UUID profileId, String name, UUID categoryId, String content) {
		Profile profile = profileRepository.findById(profileId)
				.orElseThrow(() -> new IllegalArgumentException("Profil introuvable"));

		Category category = categoryRepository.findById(categoryId)
				.orElseGet(() -> categoryRepository.findByName("AUTRE"));

		Rubric lastRubric = rubricRepository.findFirstByProfileIdOrderByDisplayOrderDesc(profileId);
		int newOrder = 0;
		if (lastRubric != null && lastRubric.getDisplayOrder() != null) {
			newOrder = lastRubric.getDisplayOrder() + 1;
		}

		Rubric rubric = new Rubric();
		rubric.setName(name);
		rubric.setCategory(category);
		rubric.setContent(content);
		rubric.setDisplayOrder(newOrder);
		rubric.setVisible(true);
		rubric.setProfile(profile);

		return rubricRepository.save(rubric);
	}
}