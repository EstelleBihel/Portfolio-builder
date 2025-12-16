package alt.portfolio.builder.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.UserRepository;
import alt.portfolio.builder.services.ProfileService; // <--- Import du service

@Controller
@RequestMapping("/profiles")
public class ProfileController {

	@Autowired
	private ProfileService profileService; // <--- On injecte le Service, plus le Repository

	@Autowired
	private UserRepository userRepository; // Garde ça juste pour le test de création rapide

	// 1. AFFICHER LA LISTE
	@GetMapping("")
	public ModelAndView index() {
		// On utilise le service ici
		return new ModelAndView("profiles/index", "profiles", profileService.getAllProfiles());
	}

	// 2. AFFICHER LE FORMULAIRE D'EDITION
	@GetMapping("/{id}/edit")
	public ModelAndView edit(@PathVariable UUID id) {
		// On utilise le service ici aussi
		Profile profile = profileService.getProfileById(id);
		return new ModelAndView("profiles/edit", "profile", profile);
	}

	// 3. CREATION Profil - US006
	@PostMapping("/user/{userId}/create")
	public String createProfile(@PathVariable UUID userId, @RequestParam String name) {
		// Récupération de l'utilisateur
		User owner = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

		// Création propre du profil
		Profile p = new Profile();
		p.setName(name);
		p.setDescription("Description par défaut");
		p.setOwner(owner); // On associe le profil à l'utilisateur récupéré

		// Sauvegarde via le service
		profileService.saveProfile(p);

		// Redirection vers la liste des profils de cet utilisateur
		return "redirect:/profiles/user/" + userId;
	}

	// Route pour afficher les profils d'un utilisateur spécifique
	// URL ex: /profiles/user/123e4567-e89b...
	@GetMapping("/user/{userId}")
	public ModelAndView listProfilesByUser(@PathVariable UUID userId) {
		ModelAndView mv = new ModelAndView("profiles/index");

		// On envoie la liste des profils
		mv.addObject("profiles", profileService.getProfilesByUser(userId));

		// On envoie aussi l'utilisateur propriétaire pour le formulaire de création
		User owner = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
		mv.addObject("owner", owner);

		return mv;
	}
}