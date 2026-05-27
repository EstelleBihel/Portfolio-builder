package alt.portfolio.builder.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import alt.portfolio.builder.entities.Element;
import alt.portfolio.builder.entities.Rubric;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.services.ElementService;
import alt.portfolio.builder.services.RubricService;

//Controleur pour la gestion des elements 
@Controller
public class ElementController {

	@Autowired
	private ElementService elementService;

	@Autowired
	private RubricService rubricService;

	// US-017 : Ajouter un element a une rubrique POST
	// /rubrics/{rubricId}/elements/add
	@PostMapping("/rubrics/{rubricId}/elements/add")
	public String addElement(@PathVariable UUID rubricId, @RequestParam String title,
			@RequestParam(required = false) String subtitle, @RequestParam(required = false) String description,
			@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
			@RequestParam(required = false) String location, @RequestParam(required = false) String link,
			Authentication auth, RedirectAttributes redirectAttributes) {

		Rubric rubric = rubricService.getRubricById(rubricId);
		if (rubric == null) {
			redirectAttributes.addFlashAttribute("error", "Rubrique introuvable.");
			return "redirect:/dashboard";
		}

		User currentUser = (User) auth.getPrincipal();
		if (!rubric.getProfile().getOwner().getId().equals(currentUser.getId())) {
			redirectAttributes.addFlashAttribute("error", "Vous n'avez pas le droit de modifier cette rubrique.");
			return "redirect:/profiles/user/" + currentUser.getId();
		}

		// Validation
		if (title == null || title.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Le titre de l'element est obligatoire.");
			return "redirect:/rubrics/" + rubricId + "/edit";
		}

		title = title.trim();
		if (title.length() > 100) {
			title = title.substring(0, 100);
		}

		try {
			elementService.createElement(rubricId, title, subtitle != null ? subtitle.trim() : null,
					description != null ? description.trim() : null,
					startDate != null && !startDate.trim().isEmpty() ? startDate.trim() : null,
					endDate != null && !endDate.trim().isEmpty() ? endDate.trim() : null,
					location != null ? location.trim() : null, link != null ? link.trim() : null);

			redirectAttributes.addFlashAttribute("success", "L'element \"" + title + "\" a ete ajoute.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout : " + e.getMessage());
		}

		return "redirect:/rubrics/" + rubricId + "/edit";
	}

	// US-018 : Afficher le formulaire de modification d'un element GET
	// elements/{id}/edit
	@GetMapping("/elements/{id}/edit")
	public ModelAndView editElementForm(@PathVariable UUID id, Authentication auth) {
		Element element = elementService.getElementById(id);

		if (element == null) {
			return new ModelAndView("redirect:/dashboard");
		}

		User currentUser = (User) auth.getPrincipal();
		if (!element.getRubric().getProfile().getOwner().getId().equals(currentUser.getId())) {
			return new ModelAndView("redirect:/profiles/user/" + currentUser.getId());
		}

		ModelAndView mv = new ModelAndView("elements/edit");
		mv.addObject("element", element);
		mv.addObject("rubric", element.getRubric());
		mv.addObject("profile", element.getRubric().getProfile());
		return mv;
	}

	// US-018 : Enregistrer la modification d'un element POST /elements/{id}/update
	@PostMapping("/elements/{id}/update")
	public String updateElement(@PathVariable UUID id, @RequestParam String title,
			@RequestParam(required = false) String subtitle, @RequestParam(required = false) String description,
			@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
			@RequestParam(required = false) String location, @RequestParam(required = false) String link,
			Authentication auth, RedirectAttributes redirectAttributes) {

		Element element = elementService.getElementById(id);

		if (element == null) {
			redirectAttributes.addFlashAttribute("error", "Element introuvable.");
			return "redirect:/dashboard";
		}

		User currentUser = (User) auth.getPrincipal();
		if (!element.getRubric().getProfile().getOwner().getId().equals(currentUser.getId())) {
			redirectAttributes.addFlashAttribute("error", "Vous n'avez pas le droit de modifier cet element.");
			return "redirect:/profiles/user/" + currentUser.getId();
		}

		// Validation
		if (title == null || title.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Le titre de l'element est obligatoire.");
			return "redirect:/elements/" + id + "/edit";
		}

		title = title.trim();
		if (title.length() > 100) {
			title = title.substring(0, 100);
		}

		try {
			element.setTitle(title);
			element.setSubtitle(subtitle != null ? subtitle.trim() : null);
			element.setDescription(description != null ? description.trim() : null);
			element.setStartDate(startDate != null && !startDate.trim().isEmpty() ? startDate.trim() : null);
			element.setEndDate(endDate != null && !endDate.trim().isEmpty() ? endDate.trim() : null);
			element.setLocationEntity(elementService.resolveLocation(location));
			element.setLink(link != null ? link.trim() : null);

			elementService.saveElement(element);
			redirectAttributes.addFlashAttribute("success", "L'element a ete mis a jour.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise a jour : " + e.getMessage());
		}

		return "redirect:/rubrics/" + element.getRubric().getId() + "/edit";
	}

	// US-019 : Supprimer un element POST /elements/{id}/delete
	@PostMapping("/elements/{id}/delete")
	public String deleteElement(@PathVariable UUID id, Authentication auth, RedirectAttributes redirectAttributes) {

		Element element = elementService.getElementById(id);

		if (element == null) {
			redirectAttributes.addFlashAttribute("error", "Element introuvable.");
			return "redirect:/dashboard";
		}

		User currentUser = (User) auth.getPrincipal();
		if (!element.getRubric().getProfile().getOwner().getId().equals(currentUser.getId())) {
			redirectAttributes.addFlashAttribute("error", "Vous n'avez pas le droit de supprimer cet element.");
			return "redirect:/profiles/user/" + currentUser.getId();
		}

		UUID rubricId = element.getRubric().getId();
		String elementTitle = element.getTitle();

		try {
			elementService.deleteElement(id);
			redirectAttributes.addFlashAttribute("success", "L'element \"" + elementTitle + "\" a ete supprime.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
		}

		return "redirect:/rubrics/" + rubricId + "/edit";
	}

	// US-020 : Deplacer un element vers le haut POST /elements/{id}/move-up
	@PostMapping("/elements/{id}/move-up")
	public String moveUp(@PathVariable UUID id, Authentication auth) {
		Element element = elementService.getElementById(id);
		if (element == null) {
			return "redirect:/dashboard";
		}

		User currentUser = (User) auth.getPrincipal();
		if (!element.getRubric().getProfile().getOwner().getId().equals(currentUser.getId())) {
			return "redirect:/profiles/user/" + currentUser.getId();
		}

		elementService.moveUp(id);
		return "redirect:/rubrics/" + element.getRubric().getId() + "/edit";
	}

	// US-020 : Deplacer un element vers le bas POST /elements/{id}/move-down
	@PostMapping("/elements/{id}/move-down")
	public String moveDown(@PathVariable UUID id, Authentication auth) {
		Element element = elementService.getElementById(id);
		if (element == null) {
			return "redirect:/dashboard";
		}

		User currentUser = (User) auth.getPrincipal();
		if (!element.getRubric().getProfile().getOwner().getId().equals(currentUser.getId())) {
			return "redirect:/profiles/user/" + currentUser.getId();
		}

		elementService.moveDown(id);
		return "redirect:/rubrics/" + element.getRubric().getId() + "/edit";
	}
}