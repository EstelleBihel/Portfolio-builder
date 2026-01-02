package alt.portfolio.builder.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import alt.portfolio.builder.entities.User;
import jakarta.servlet.http.HttpServletRequest;

/**
 * ControllerAdvice pour fournir des données globales à toutes les vues US-039 :
 * Gestion de la navigation et de l'utilisateur actif
 */
@ControllerAdvice
public class MainAdvice {

	/**
	 * Fournit l'utilisateur connecté à toutes les vues
	 */
	@ModelAttribute("activeUser")
	public User activeUser(Authentication auth) {
		return (auth == null) ? null : (User) auth.getPrincipal();
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
		// /users mais pas /users/register (qui est public)
		return uri.startsWith("/users") && !uri.contains("/register");
	}

	/**
	 * Fournit l'année courante pour le footer
	 */
	@ModelAttribute("currentYear")
	public int currentYear() {
		return java.time.Year.now().getValue();
	}
}