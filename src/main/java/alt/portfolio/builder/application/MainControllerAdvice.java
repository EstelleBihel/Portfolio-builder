package alt.portfolio.builder.application;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.exceptions.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * ControllerAdvice principal - Fournit des données globales à toutes les vues -
 * Gère les exceptions 404
 */
@ControllerAdvice
public class MainControllerAdvice {

	// =========================================================================
	// ATTRIBUTS GLOBAUX POUR TOUTES LES VUES
	// =========================================================================

	/**
	 * Fournit l'utilisateur connecté à toutes les vues Retourne null si
	 * l'utilisateur n'est pas authentifié ou est anonyme
	 */
	@ModelAttribute("activeUser")
	public User activeUser(Authentication auth) {
		if (auth == null || auth instanceof AnonymousAuthenticationToken) {
			return null;
		}
		if (auth.getPrincipal() instanceof User) {
			return (User) auth.getPrincipal();
		}
		return null;
	}

	/**
	 * Indique si on est sur la page d'accueil
	 */
	@ModelAttribute("isHome")
	public boolean isHome(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return "/".equals(uri) || "/about".equals(uri);
	}

	/**
	 * Indique si on est sur le dashboard
	 */
	@ModelAttribute("isDashboard")
	public boolean isDashboard(HttpServletRequest request) {
		return request.getRequestURI().startsWith("/dashboard");
	}

	/**
	 * Indique si on est sur la section profils
	 */
	@ModelAttribute("isProfiles")
	public boolean isProfiles(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return uri.startsWith("/profiles") || uri.startsWith("/rubrics");
	}

	/**
	 * Indique si on est sur la section utilisateurs
	 */
	@ModelAttribute("isUsers")
	public boolean isUsers(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return uri.startsWith("/users") && !uri.contains("/register");
	}

	/**
	 * Fournit l'année courante pour le footer
	 */
	@ModelAttribute("currentYear")
	public int currentYear() {
		return java.time.Year.now().getValue();
	}

	// =========================================================================
	// GESTION DES EXCEPTIONS 404
	// =========================================================================

	/**
	 * Gère EntityNotFoundException (entité non trouvée en BDD)
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	public ModelAndView entityNotFound(EntityNotFoundException ex) {
		return new ModelAndView("errors/404", "message", ex.getMessage());
	}

	/**
	 * Gère NullPointerException
	 */
	@ExceptionHandler(NullPointerException.class)
	public ModelAndView nullPointerException() {
		return new ModelAndView("errors/404", "message", "Ressource non trouvée");
	}
}