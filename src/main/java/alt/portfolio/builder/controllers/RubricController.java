package alt.portfolio.builder.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import alt.portfolio.builder.entities.Rubric;
import alt.portfolio.builder.services.RubricService;

@Controller
public class RubricController {

	@Autowired
	private RubricService rubricService;

	// Ajouter une rubrique
	@PostMapping("/profiles/{profileId}/rubrics/add")
	public String addRubric(@PathVariable UUID profileId, @RequestParam String name) {
		rubricService.createRubric(profileId, name);
		return "redirect:/profiles/" + profileId + "/edit";
	}

	// Afficher le formulaire de modification (GET)
	@GetMapping("/rubrics/{id}/edit")
	public ModelAndView editRubricForm(@PathVariable UUID id) {
		Rubric rubric = rubricService.getRubricById(id);
		return new ModelAndView("rubrics/edit", "rubric", rubric);
	}

	// Enregistrer la modification (POST)
	// CORRECTION ICI : Ajout du préfixe "/rubrics" pour correspondre au formulaire
	@PostMapping("/rubrics/{id}/edit")
	public String updateRubric(@PathVariable UUID id, @RequestParam String name, @RequestParam Integer displayOrder) {

		Rubric rubric = rubricService.getRubricById(id);

		rubric.setName(name);
		rubric.setDisplayOrder(displayOrder);

		rubricService.saveRubric(rubric);

		// Retour au profil parent
		return "redirect:/profiles/" + rubric.getProfile().getId() + "/edit";
	}
}