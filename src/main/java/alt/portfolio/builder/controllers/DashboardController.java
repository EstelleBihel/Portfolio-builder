package alt.portfolio.builder.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.services.ProfileService;

//Contrôleur pour le tableau de bord utilisateur US-002b : Accéder à mon tableau de bord (dashboard)
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	private ProfileService profileService;

	// Affiche le tableau de bord de l'utilisateur connecté Présente un résumé de
	// son activité : profils, rubriques, etc.
	@GetMapping({ "", "/" })
	public ModelAndView index(Authentication auth) {
		ModelAndView mv = new ModelAndView("dashboard/index");

		// Récupérer l'utilisateur connecté
		User currentUser = (User) auth.getPrincipal();
		mv.addObject("user", currentUser);

		// Récupérer les profils de l'utilisateur
		List<Profile> profiles = profileService.getProfilesByUser(currentUser.getId());
		mv.addObject("profiles", profiles);

		// Statistiques pour le dashboard
		mv.addObject("profileCount", profiles.size());

		// Compter le total des rubriques
		int totalRubrics = profiles.stream().mapToInt(p -> p.getRubrics() != null ? p.getRubrics().size() : 0).sum();
		mv.addObject("rubricCount", totalRubrics);

		// Profils publiés (pour plus tard - on prépare)
		long publishedCount = profiles.stream().filter(Profile::isPublished).count();
		mv.addObject("publishedCount", publishedCount);

		return mv;
	}
}