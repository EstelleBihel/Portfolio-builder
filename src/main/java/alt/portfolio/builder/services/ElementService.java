package alt.portfolio.builder.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import alt.portfolio.builder.entities.Element;
import alt.portfolio.builder.entities.Rubric;
import alt.portfolio.builder.repositories.ElementRepository;
import alt.portfolio.builder.repositories.RubricRepository;

//Service pour la gestion des elements US-017
@Service
public class ElementService {

	@Autowired
	private ElementRepository elementRepository;

	@Autowired
	private RubricRepository rubricRepository;

	// Recuperer tous les elements d'une rubrique
	public List<Element> getElementsByRubric(UUID rubricId) {
		return elementRepository.findByRubricIdOrderByDisplayOrderAsc(rubricId);
	}

	// Recuperer un element par son ID
	public Element getElementById(UUID id) {
		return elementRepository.findById(id).orElse(null);
	}

	// US-017 : Creer un nouvel element
	@Transactional
	public Element createElement(UUID rubricId, String title, String subtitle, String description, String startDate,
			String endDate, String location, String link) {

		Rubric rubric = rubricRepository.findById(rubricId).orElse(null);
		if (rubric == null) {
			throw new IllegalArgumentException("Rubrique introuvable");
		}

		// Determiner l'ordre d'affichage
		Element lastElement = elementRepository.findFirstByRubricIdOrderByDisplayOrderDesc(rubricId);
		int newOrder = (lastElement != null) ? lastElement.getDisplayOrder() + 1 : 0;

		Element element = new Element();
		element.setTitle(title);
		element.setSubtitle(subtitle);
		element.setDescription(description);
		element.setStartDate(startDate);
		element.setEndDate(endDate);
		element.setLocation(location);
		element.setLink(link);
		element.setDisplayOrder(newOrder);
		element.setRubric(rubric);

		return elementRepository.save(element);
	}

	// US-018 : Sauvegarder un element (creation ou mise a jour)
	public Element saveElement(Element element) {
		return elementRepository.save(element);
	}

	// US-019 : Supprimer un element
	@Transactional
	public void deleteElement(UUID id) {
		elementRepository.deleteById(id);
	}

	// US-020 : Deplacer un element vers le haut
	@Transactional
	public void moveUp(UUID elementId) {
		Element element = elementRepository.findById(elementId).orElse(null);
		if (element == null)
			return;

		List<Element> elements = elementRepository.findByRubricIdOrderByDisplayOrderAsc(element.getRubric().getId());
		int index = -1;

		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).getId().equals(elementId)) {
				index = i;
				break;
			}
		}

		if (index > 0) {
			Element previous = elements.get(index - 1);
			int tempOrder = element.getDisplayOrder();
			element.setDisplayOrder(previous.getDisplayOrder());
			previous.setDisplayOrder(tempOrder);

			elementRepository.save(element);
			elementRepository.save(previous);
		}
	}

	// US-020 : Deplacer un element vers le bas
	@Transactional
	public void moveDown(UUID elementId) {
		Element element = elementRepository.findById(elementId).orElse(null);
		if (element == null)
			return;

		List<Element> elements = elementRepository.findByRubricIdOrderByDisplayOrderAsc(element.getRubric().getId());
		int index = -1;

		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).getId().equals(elementId)) {
				index = i;
				break;
			}
		}

		if (index >= 0 && index < elements.size() - 1) {
			Element next = elements.get(index + 1);
			int tempOrder = element.getDisplayOrder();
			element.setDisplayOrder(next.getDisplayOrder());
			next.setDisplayOrder(tempOrder);

			elementRepository.save(element);
			elementRepository.save(next);
		}
	}

	// Compter les elements d'une rubrique
	public long countByRubric(UUID rubricId) {
		return elementRepository.countByRubricId(rubricId);
	}
}