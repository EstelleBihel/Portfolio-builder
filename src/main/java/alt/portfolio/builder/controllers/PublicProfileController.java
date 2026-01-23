package alt.portfolio.builder.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import alt.portfolio.builder.entities.Element;
import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.Rubric;
import alt.portfolio.builder.services.ElementService;
import alt.portfolio.builder.services.ProfileService;
import alt.portfolio.builder.services.RubricService;

/**
 * Controleur pour l'affichage public des profils EPIC 4 : Publication et
 * partage
 * 
 * US-021 : Prévisualiser mon profil US-022 : Publier mon profil en CV et/ou
 * Portfolio US-027 : Voir mes deux URL publiques
 */
@Controller
public class PublicProfileController {

	@Autowired
	private ProfileService profileService;

	@Autowired
	private RubricService rubricService;

	@Autowired
	private ElementService elementService;

	// =========================================================================
	// US-027 : PAGE PUBLIQUE PORTFOLIO - /p/{slug}
	// Vue détaillée avec projets visuels
	// =========================================================================

	@GetMapping("/p/{slug}")
	public ModelAndView viewPublicPortfolio(@PathVariable String slug) {
		Profile profile = profileService.getProfileBySlug(slug);

		// Profil introuvable
		if (profile == null) {
			return notFoundView("Ce profil n'existe pas.");
		}

		// Portfolio non publié
		if (!profile.isPortfolioPublished()) {
			return notFoundView("Ce portfolio n'est pas accessible.");
		}

		// Charger les rubriques visibles avec leurs éléments
		List<Rubric> rubrics = rubricService.getVisibleRubricsByProfile(profile.getId());
		loadElementsForRubrics(rubrics);

		ModelAndView mv = new ModelAndView("public/portfolio");
		mv.addObject("profile", profile);
		mv.addObject("rubrics", rubrics);
		mv.addObject("owner", profile.getOwner());
		mv.addObject("viewMode", "portfolio");
		return mv;
	}

	// =========================================================================
	// US-027 : PAGE PUBLIQUE CV - /cv/{slug}
	// Vue épurée, focus sur le parcours
	// =========================================================================

	@GetMapping("/cv/{slug}")
	public ModelAndView viewPublicCv(@PathVariable String slug) {
		Profile profile = profileService.getProfileBySlug(slug);

		// Profil introuvable
		if (profile == null) {
			return notFoundView("Ce CV n'existe pas.");
		}

		// CV non publié
		if (!profile.isCvPublished()) {
			return notFoundView("Ce CV n'est pas accessible.");
		}

		// Charger les rubriques visibles avec leurs éléments
		List<Rubric> rubrics = rubricService.getVisibleRubricsByProfile(profile.getId());
		loadElementsForRubrics(rubrics);

		ModelAndView mv = new ModelAndView("public/cv");
		mv.addObject("profile", profile);
		mv.addObject("rubrics", rubrics);
		mv.addObject("owner", profile.getOwner());
		mv.addObject("viewMode", "cv");
		return mv;
	}

	// =========================================================================
	// MÉTHODES UTILITAIRES
	// =========================================================================

	/**
	 * Charge les éléments pour chaque rubrique
	 */
	private void loadElementsForRubrics(List<Rubric> rubrics) {
		for (Rubric rubric : rubrics) {
			List<Element> elements = elementService.getElementsByRubric(rubric.getId());
			rubric.setElements(elements);
		}
	}

	/**
	 * Vue "profil non trouvé"
	 */
	private ModelAndView notFoundView(String message) {
		ModelAndView mv = new ModelAndView("public/not-found");
		mv.addObject("message", message);
		return mv;
	}
}