package alt.portfolio.builder.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

//Contrôleur pour la page d'accueil publique US-000 : Accéder à la page d'accueil publique
@Controller
public class HomeController {

	// Page d'accueil publique accessible à tous Si l'utilisateur est connecté, on
	// lui propose d'accéder à son dashboard
	@GetMapping("/")
	public ModelAndView home(Authentication auth) {
		ModelAndView mv = new ModelAndView("home/index");

		// Si l'utilisateur est connecté, on passe cette info à la vue
		mv.addObject("isAuthenticated", auth != null && auth.isAuthenticated());

		return mv;
	}

	// Page "À propos" (optionnelle mais utile pour le portfolio)
	@GetMapping("/about")
	public ModelAndView about() {
		return new ModelAndView("home/about");
	}
}