package alt.portfolio.builder.controllers;

import java.util.List;
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

import alt.portfolio.builder.entities.Category;
import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.Rubric;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.services.ElementService;
import alt.portfolio.builder.services.ProfileService;
import alt.portfolio.builder.services.RubricService;

//Controleur pour la gestion des rubriques
@Controller
public class RubricController {
	@Autowired
	private ElementService elementService;

	@Autowired
	private RubricService rubricService;

	@Autowired
	private ProfileService profileService;

	// US-012 : Ajouter une rubrique a un profil POST
	// /profiles/{profileId}/rubrics/add*/
	// US-012 : Ajouter une rubrique a un profil POST
	// /profiles/{profileId}/rubrics/add
	@PostMapping("/profiles/{profileId}/rubrics/add")
	public String addRubric(@PathVariable UUID profileId, @RequestParam String name,
			@RequestParam(required = false) UUID categoryId, @RequestParam(required = false) String content,
			Authentication auth, RedirectAttributes redirectAttributes) {

		Profile profile = profileService.getProfileById(profileId);
		if (profile == null) {
			redirectAttributes.addFlashAttribute("error", "Profil introuvable.");
			return "redirect:/dashboard";
		}

		User currentUser = (User) auth.getPrincipal();
		if (!profile.getOwner().getId().equals(currentUser.getId())) {
			redirectAttributes.addFlashAttribute("error", "Vous n'avez pas le droit de modifier ce profil.");
			return "redirect:/profiles/user/" + currentUser.getId();
		}

		if (name == null || name.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Le nom de la rubrique est obligatoire.");
			return "redirect:/profiles/" + profileId + "/edit";
		}

		name = name.trim();
		if (name.length() > 100) {
			name = name.substring(0, 100);
		}

		try {
			if (categoryId != null) {
				rubricService.createRubricWithCategory(profileId, name, categoryId, content);
			} else {
				// fallback si aucune catégorie fournie
				rubricService.createRubric(profileId, name, "AUTRE", content);
			}
			redirectAttributes.addFlashAttribute("success", "La rubrique \"" + name + "\" a ete ajoutee.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout : " + e.getMessage());
		}

		return "redirect:/profiles/" + profileId + "/edit";
	}

	// US-013 : Afficher le formulaire de modification d'une rubrique GET
	// rubrics/{id}/edit MISE A JOUR : Inclut maintenant les elements
	@GetMapping("/rubrics/{id}/edit")
	public ModelAndView editRubricForm(@PathVariable UUID id, Authentication auth) {
		Rubric rubric = rubricService.getRubricById(id);

		if (rubric == null) {
			return new ModelAndView("redirect:/dashboard");
		}

		// Verifier que l'utilisateur est proprietaire du profil
		User currentUser = (User) auth.getPrincipal();
		if (!rubric.getProfile().getOwner().getId().equals(currentUser.getId())) {
			return new ModelAndView("redirect:/profiles/user/" + currentUser.getId());
		}

		ModelAndView mv = new ModelAndView("rubrics/edit");
		mv.addObject("rubric", rubric);
		mv.addObject("profile", rubric.getProfile());

		// US-017-020 : Ajouter les elements
		mv.addObject("elements", elementService.getElementsByRubric(id));
		mv.addObject("elementCount", elementService.countByRubric(id));

		// Liste des catégories avec marquage de celle de la rubrique courante
		List<Category> categories = rubricService.getAllCategories();
		UUID currentCatId = rubric.getCategory() != null ? rubric.getCategory().getId() : null;
		List<java.util.Map<String, Object>> categoryOptions = new java.util.ArrayList<>();
		for (Category c : categories) {
			java.util.Map<String, Object> opt = new java.util.HashMap<>();
			opt.put("id", c.getId());
			opt.put("name", c.getName());
			opt.put("selected", c.getId().equals(currentCatId));
			categoryOptions.add(opt);
		}
		mv.addObject("categories", categoryOptions);

		return mv;
	}

	// US-013 : Enregistrer la modification d'une rubrique POST /rubrics/{id}/update
	@PostMapping("/rubrics/{id}/update")
	public String updateRubric(@PathVariable UUID id, @RequestParam String name,
			@RequestParam(required = false) UUID categoryId, @RequestParam(required = false) String content,
			@RequestParam(required = false) Integer displayOrder, Authentication auth,
			RedirectAttributes redirectAttributes) {

		Rubric rubric = rubricService.getRubricById(id);

		if (rubric == null) {
			redirectAttributes.addFlashAttribute("error", "Rubrique introuvable.");
			return "redirect:/dashboard";
		}

		// Verifier que l'utilisateur est proprietaire
		User currentUser = (User) auth.getPrincipal();
		if (!rubric.getProfile().getOwner().getId().equals(currentUser.getId())) {
			redirectAttributes.addFlashAttribute("error", "Vous n'avez pas le droit de modifier cette rubrique.");
			return "redirect:/profiles/user/" + currentUser.getId();
		}

		// Validation
		if (name == null || name.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Le nom de la rubrique est obligatoire.");
			return "redirect:/rubrics/" + id + "/edit";
		}

		name = name.trim();
		if (name.length() > 100) {
			name = name.substring(0, 100);
		}

		try {
			rubric.setName(name);
			if (categoryId != null) {
				rubricService.getAllCategories().stream().filter(c -> c.getId().equals(categoryId)).findFirst()
						.ifPresent(c -> {
							rubric.setCategory(c);
						});
			}
			if (content != null) {
				rubric.setContent(content.trim());
			}
			if (displayOrder != null) {
				rubric.setDisplayOrder(displayOrder);
			}

			rubricService.saveRubric(rubric);
			redirectAttributes.addFlashAttribute("success", "La rubrique a ete mise a jour.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise a jour : " + e.getMessage());
		}

		return "redirect:/profiles/" + rubric.getProfile().getId() + "/edit";
	}

	// US-014 : Supprimer une rubrique POST /rubrics/{id}/delete
	@PostMapping("/rubrics/{id}/delete")
	public String deleteRubric(@PathVariable UUID id, Authentication auth, RedirectAttributes redirectAttributes) {

		Rubric rubric = rubricService.getRubricById(id);

		if (rubric == null) {
			redirectAttributes.addFlashAttribute("error", "Rubrique introuvable.");
			return "redirect:/dashboard";
		}

		// Verifier que l'utilisateur est proprietaire
		User currentUser = (User) auth.getPrincipal();
		if (!rubric.getProfile().getOwner().getId().equals(currentUser.getId())) {
			redirectAttributes.addFlashAttribute("error", "Vous n'avez pas le droit de supprimer cette rubrique.");
			return "redirect:/profiles/user/" + currentUser.getId();
		}

		UUID profileId = rubric.getProfile().getId();
		String rubricName = rubric.getName();

		try {
			rubricService.deleteRubric(id);
			redirectAttributes.addFlashAttribute("success", "La rubrique \"" + rubricName + "\" a ete supprimee.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
		}

		return "redirect:/profiles/" + profileId + "/edit";
	}

	// US-016 : Changer la visibilite d'une rubrique POST
	// /rubrics/{id}/toggle-visibility
	@PostMapping("/rubrics/{id}/toggle-visibility")
	public String toggleVisibility(@PathVariable UUID id, Authentication auth, RedirectAttributes redirectAttributes) {

		Rubric rubric = rubricService.getRubricById(id);

		if (rubric == null) {
			redirectAttributes.addFlashAttribute("error", "Rubrique introuvable.");
			return "redirect:/dashboard";
		}

		User currentUser = (User) auth.getPrincipal();
		if (!rubric.getProfile().getOwner().getId().equals(currentUser.getId())) {
			return "redirect:/profiles/user/" + currentUser.getId();
		}

		try {
			rubricService.toggleVisibility(id);
			String status = rubric.getVisible() ? "masquee" : "visible";
			redirectAttributes.addFlashAttribute("success", "La rubrique est maintenant " + status + ".");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
		}

		return "redirect:/profiles/" + rubric.getProfile().getId() + "/edit";
	}

	// US-015 : Deplacer une rubrique vers le haut POST /rubrics/{id}/move-up
	@PostMapping("/rubrics/{id}/move-up")
	public String moveUp(@PathVariable UUID id, Authentication auth) {
		Rubric rubric = rubricService.getRubricById(id);
		if (rubric == null) {
			return "redirect:/dashboard";
		}

		User currentUser = (User) auth.getPrincipal();
		if (!rubric.getProfile().getOwner().getId().equals(currentUser.getId())) {
			return "redirect:/profiles/user/" + currentUser.getId();
		}

		rubricService.moveUp(id);
		return "redirect:/profiles/" + rubric.getProfile().getId() + "/edit";
	}

	// US-015 : Deplacer une rubrique vers le bas POST /rubrics/{id}/move-down
	@PostMapping("/rubrics/{id}/move-down")
	public String moveDown(@PathVariable UUID id, Authentication auth) {
		Rubric rubric = rubricService.getRubricById(id);
		if (rubric == null) {
			return "redirect:/dashboard";
		}

		User currentUser = (User) auth.getPrincipal();
		if (!rubric.getProfile().getOwner().getId().equals(currentUser.getId())) {
			return "redirect:/profiles/user/" + currentUser.getId();
		}

		rubricService.moveDown(id);
		return "redirect:/profiles/" + rubric.getProfile().getId() + "/edit";
	}
}