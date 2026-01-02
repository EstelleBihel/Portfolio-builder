package alt.portfolio.builder.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.UserRepository;
import alt.portfolio.builder.services.ProfileService;
import alt.portfolio.builder.services.RubricService;

/**
 * Controleur pour la gestion des profils
 */
@Controller
@RequestMapping("/profiles")
public class ProfileController {

	@Autowired
	private ProfileService profileService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RubricService rubricService;

	/**
	 * Afficher tous les profils (admin)
	 */
	@GetMapping("")
	public ModelAndView index() {
		return new ModelAndView("profiles/index", "profiles", profileService.getAllProfiles());
	}

	/**
	 * Afficher les profils d'un utilisateur specifique
	 */
	@GetMapping("/user/{userId}")
	public ModelAndView listProfilesByUser(@PathVariable UUID userId, Authentication auth) {
		ModelAndView mv = new ModelAndView("profiles/index");

		User currentUser = (User) auth.getPrincipal();

		if (!currentUser.getId().equals(userId)) {
			return new ModelAndView("redirect:/profiles/user/" + currentUser.getId());
		}

		User owner = userRepository.findById(currentUser.getId()).orElse(currentUser);

		mv.addObject("profiles", profileService.getProfilesByUser(userId));
		mv.addObject("owner", owner);

		return mv;
	}

	/**
	 * Creer un nouveau profil
	 */
	@PostMapping("/user/{userId}/create")
	public String createProfile(@PathVariable UUID userId, @RequestParam String name,
			@RequestParam(required = false) String description, Authentication auth,
			RedirectAttributes redirectAttributes) {

		User currentUser = (User) auth.getPrincipal();
		if (!currentUser.getId().equals(userId)) {
			redirectAttributes.addFlashAttribute("error",
					"Vous ne pouvez pas creer un profil pour un autre utilisateur.");
			return "redirect:/profiles/user/" + currentUser.getId();
		}

		if (name == null || name.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Le nom du profil est obligatoire.");
			return "redirect:/profiles/user/" + userId;
		}

		name = name.trim();
		if (name.length() > 65) {
			name = name.substring(0, 65);
		}

		if (description != null) {
			description = description.trim();
			if (description.isEmpty()) {
				description = null;
			} else if (description.length() > 500) {
				description = description.substring(0, 500);
			}
		}

		try {
			User owner = userRepository.findById(currentUser.getId())
					.orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable en base de donnees"));

			Profile profile = new Profile();
			profile.setName(name);
			profile.setDescription(description);
			profile.setOwner(owner);
			profile.setPublished(false);

			String slug = generateSlug(name, owner.getId());
			profile.setSlug(slug);

			profileService.saveProfile(profile);

			redirectAttributes.addFlashAttribute("success", "Le profil \"" + name + "\" a ete cree avec succes !");

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erreur lors de la creation du profil : " + e.getMessage());
		}

		return "redirect:/profiles/user/" + userId;
	}

	/**
	 * Afficher le formulaire d'edition d'un profil
	 */
	@GetMapping("/{id}/edit")
	public ModelAndView edit(@PathVariable UUID id, Authentication auth) {
		Profile profile = profileService.getProfileById(id);

		if (profile == null) {
			return new ModelAndView("redirect:/dashboard");
		}

		User currentUser = (User) auth.getPrincipal();
		if (!profile.getOwner().getId().equals(currentUser.getId())) {
			return new ModelAndView("redirect:/profiles/user/" + currentUser.getId());
		}

		ModelAndView mv = new ModelAndView("profiles/edit");
		mv.addObject("profile", profile);
		// IMPORTANT : Envoyer les rubriques separement pour eviter la double boucle
		// Mustache
		mv.addObject("rubrics", rubricService.getRubricsByProfile(id));
		return mv;
	}

	/**
	 * Mettre a jour un profil
	 */
	@PostMapping("/{id}/update")
	public String updateProfile(@PathVariable UUID id, @RequestParam String name,
			@RequestParam(required = false) String description, @RequestParam(required = false) String slug,
			@RequestParam(required = false) String isPublished, Authentication auth,
			RedirectAttributes redirectAttributes) {

		Profile profile = profileService.getProfileById(id);

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
			redirectAttributes.addFlashAttribute("error", "Le nom du profil est obligatoire.");
			return "redirect:/profiles/" + id + "/edit";
		}

		name = name.trim();
		if (name.length() > 65) {
			name = name.substring(0, 65);
		}

		if (description != null) {
			description = description.trim();
			if (description.isEmpty()) {
				description = null;
			} else if (description.length() > 500) {
				description = description.substring(0, 500);
			}
		}

		if (slug != null) {
			slug = slug.trim().toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-")
					.replaceAll("-+", "-").replaceAll("^-|-$", "");
			if (slug.isEmpty()) {
				slug = generateSlug(name, currentUser.getId());
			} else if (slug.length() > 100) {
				slug = slug.substring(0, 100);
			}
		}

		try {
			profile.setName(name);
			profile.setDescription(description);
			profile.setSlug(slug);

			boolean published = isPublished != null && (isPublished.equals("on") || isPublished.equals("true"));
			profile.setPublished(published);

			profileService.saveProfile(profile);

			redirectAttributes.addFlashAttribute("success", "Le profil a ete mis a jour avec succes !");

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise a jour : " + e.getMessage());
		}

		return "redirect:/profiles/" + id + "/edit";
	}

	/**
	 * Supprimer un profil
	 */
	@PostMapping("/{id}/delete")
	public String deleteProfile(@PathVariable UUID id, Authentication auth, RedirectAttributes redirectAttributes) {

		Profile profile = profileService.getProfileById(id);

		if (profile == null) {
			redirectAttributes.addFlashAttribute("error", "Profil introuvable.");
			return "redirect:/dashboard";
		}

		User currentUser = (User) auth.getPrincipal();
		if (!profile.getOwner().getId().equals(currentUser.getId())) {
			redirectAttributes.addFlashAttribute("error", "Vous n'avez pas le droit de supprimer ce profil.");
			return "redirect:/profiles/user/" + currentUser.getId();
		}

		try {
			String profileName = profile.getName();
			profileService.deleteProfile(id);
			redirectAttributes.addFlashAttribute("success", "Le profil \"" + profileName + "\" a ete supprime.");

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
		}

		return "redirect:/profiles/user/" + currentUser.getId();
	}

	/**
	 * Generer un slug unique
	 */
	private String generateSlug(String name, UUID userId) {
		String baseSlug = name.toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-")
				.replaceAll("-+", "-").replaceAll("^-|-$", "");

		if (baseSlug.length() > 50) {
			baseSlug = baseSlug.substring(0, 50);
		}

		String suffix = userId.toString().substring(0, 8);

		return baseSlug + "-" + suffix;
	}
}