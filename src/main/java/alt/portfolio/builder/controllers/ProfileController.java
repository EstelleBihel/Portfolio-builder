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

import alt.portfolio.builder.entities.Element;
import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.Rubric;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.services.ElementService;
import alt.portfolio.builder.services.ProfileService;
import alt.portfolio.builder.services.RubricService;

//Controleur pour la gestion des profils EPIC 4 : Publication et partage
@Controller
public class ProfileController {

	@Autowired
	private ProfileService profileService;

	@Autowired
	private RubricService rubricService;

	@Autowired
	private ElementService elementService;

	// =========================================================================
	// LISTE DES PROFILS - US-007
	// =========================================================================

	@GetMapping("/profiles/user/{userId}")
	public ModelAndView listProfiles(@PathVariable UUID userId, Authentication auth) {
		User currentUser = (User) auth.getPrincipal();

		if (!currentUser.getId().equals(userId)) {
			return new ModelAndView("redirect:/dashboard");
		}

		List<Profile> profiles = profileService.getProfilesByUser(userId);

		ModelAndView mv = new ModelAndView("profiles/index");
		mv.addObject("profiles", profiles);
		mv.addObject("user", currentUser);
		mv.addObject("isEmpty", profiles.isEmpty());
		return mv;
	}

	// =========================================================================
	// CRÉATION DE PROFIL - US-006
	// =========================================================================

	@PostMapping("/profiles/user/{userId}/create")
	public String createProfile(@PathVariable UUID userId, @RequestParam String name,
			@RequestParam(required = false) String description, Authentication auth,
			RedirectAttributes redirectAttributes) {

		User currentUser = (User) auth.getPrincipal();

		if (!currentUser.getId().equals(userId)) {
			return "redirect:/dashboard";
		}

		Profile profile = profileService.createProfile(userId, name, description);
		redirectAttributes.addFlashAttribute("success", "Profil '" + name + "' créé avec succès !");

		return "redirect:/profiles/" + profile.getId() + "/edit";
	}

	// =========================================================================
	// ÉDITION DE PROFIL - US-008
	// =========================================================================

	@GetMapping("/profiles/{id}/edit")
	public ModelAndView editProfile(@PathVariable UUID id, Authentication auth) {
		User currentUser = (User) auth.getPrincipal();
		Profile profile = profileService.getProfileById(id);

		if (profile == null || !profile.getOwner().getId().equals(currentUser.getId())) {
			return new ModelAndView("redirect:/dashboard");
		}

		List<Rubric> rubrics = rubricService.getRubricsByProfile(id);

		ModelAndView mv = new ModelAndView("profiles/edit");
		mv.addObject("profile", profile);
		mv.addObject("rubrics", rubrics);
		mv.addObject("user", currentUser);
		return mv;
	}

	@PostMapping("/profiles/{id}/update")
	public String updateProfile(@PathVariable UUID id, @RequestParam String name,
			@RequestParam(required = false) String description, @RequestParam(required = false) String slug,
			@RequestParam(required = false) Boolean isPublishedPortfolio,
			@RequestParam(required = false) Boolean isPublishedCv, Authentication auth,
			RedirectAttributes redirectAttributes) {

		User currentUser = (User) auth.getPrincipal();
		Profile profile = profileService.getProfileById(id);

		if (profile == null || !profile.getOwner().getId().equals(currentUser.getId())) {
			return "redirect:/dashboard";
		}

		profile.setName(name);
		profile.setDescription(description);

		// Mise à jour du slug si fourni
		if (slug != null && !slug.trim().isEmpty()) {
			profile.setSlug(slug.trim());
		}

		// US-022 : Publication CV et/ou Portfolio
		profile.setIsPublishedPortfolio(isPublishedPortfolio != null && isPublishedPortfolio);
		profile.setIsPublishedCv(isPublishedCv != null && isPublishedCv);

		profileService.saveProfile(profile);
		redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès !");

		return "redirect:/profiles/" + id + "/edit";
	}

	// =========================================================================
	// US-022 : PUBLICATION RAPIDE CV / PORTFOLIO
	// =========================================================================

	@PostMapping("/profiles/{id}/toggle-portfolio")
	public String togglePortfolioPublication(@PathVariable UUID id, Authentication auth,
			RedirectAttributes redirectAttributes) {
		User currentUser = (User) auth.getPrincipal();
		Profile profile = profileService.getProfileById(id);

		if (profile == null || !profile.getOwner().getId().equals(currentUser.getId())) {
			return "redirect:/dashboard";
		}

		// Toggle publication Portfolio
		boolean newState = !profile.isPortfolioPublished();
		profile.setIsPublishedPortfolio(newState);
		profileService.saveProfile(profile);

		String message = newState ? "Portfolio publié !" : "Portfolio dépublié.";
		redirectAttributes.addFlashAttribute("success", message);

		return "redirect:/profiles/" + id + "/edit";
	}

	@PostMapping("/profiles/{id}/toggle-cv")
	public String toggleCvPublication(@PathVariable UUID id, Authentication auth,
			RedirectAttributes redirectAttributes) {
		User currentUser = (User) auth.getPrincipal();
		Profile profile = profileService.getProfileById(id);

		if (profile == null || !profile.getOwner().getId().equals(currentUser.getId())) {
			return "redirect:/dashboard";
		}

		// Toggle publication CV
		boolean newState = !profile.isCvPublished();
		profile.setIsPublishedCv(newState);
		profileService.saveProfile(profile);

		String message = newState ? "CV publié !" : "CV dépublié.";
		redirectAttributes.addFlashAttribute("success", message);

		return "redirect:/profiles/" + id + "/edit";
	}

	// =========================================================================
	// US-021 : PRÉVISUALISATION (mode Portfolio ou CV)
	// =========================================================================

	@GetMapping("/profiles/{id}/preview")
	public ModelAndView previewProfile(@PathVariable UUID id, @RequestParam(defaultValue = "portfolio") String mode,
			Authentication auth) {
		User currentUser = (User) auth.getPrincipal();
		Profile profile = profileService.getProfileById(id);

		if (profile == null || !profile.getOwner().getId().equals(currentUser.getId())) {
			return new ModelAndView("redirect:/dashboard");
		}

		// Charger les rubriques visibles avec leurs éléments
		List<Rubric> rubrics = rubricService.getVisibleRubricsByProfile(id);
		for (Rubric rubric : rubrics) {
			List<Element> elements = elementService.getElementsByRubric(rubric.getId());
			rubric.setElements(elements);
		}

		// US-026 : Choisir le template selon le mode
		String templateName = "cv".equals(mode) ? "preview/cv" : "preview/portfolio";

		ModelAndView mv = new ModelAndView(templateName);
		mv.addObject("profile", profile);
		mv.addObject("rubrics", rubrics);
		mv.addObject("owner", profile.getOwner());
		mv.addObject("viewMode", mode);
		mv.addObject("isPreview", true);
		return mv;
	}

	// =========================================================================
	// SUPPRESSION DE PROFIL - US-010
	// =========================================================================

	@PostMapping("/profiles/{id}/delete")
	public String deleteProfile(@PathVariable UUID id, Authentication auth, RedirectAttributes redirectAttributes) {
		User currentUser = (User) auth.getPrincipal();
		Profile profile = profileService.getProfileById(id);

		if (profile == null || !profile.getOwner().getId().equals(currentUser.getId())) {
			return "redirect:/dashboard";
		}

		String profileName = profile.getName();
		profileService.deleteProfile(id);
		redirectAttributes.addFlashAttribute("success", "Profil '" + profileName + "' supprimé.");

		return "redirect:/dashboard";
	}
}