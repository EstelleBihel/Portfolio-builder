package alt.portfolio.builder.controllers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import alt.portfolio.builder.entities.User;
import jakarta.servlet.http.HttpServletRequest;

//ControllerAdvice pour fournir des donnees globales a toutes les vues 
@ControllerAdvice
public class MainAdvice {

	// Fournit l'utilisateur connecte a toutes les vues Retourne null si
	// l'utilisateur n'est pas authentifie ou est anonyme
	@ModelAttribute("activeUser")
	public User activeUser(Authentication auth) {
		// Verifier que auth existe et n'est pas un token anonyme
		if (auth == null || auth instanceof AnonymousAuthenticationToken) {
			return null;
		}

		// Verifier que le principal est bien un User
		if (auth.getPrincipal() instanceof User) {
			return (User) auth.getPrincipal();
		}

		return null;
	}

	// Indique si on est sur la page d'accueil
	@ModelAttribute("isHome")
	public boolean isHome(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return "/".equals(uri) || "/about".equals(uri);
	}

	// Indique si on est sur le dashboard
	@ModelAttribute("isDashboard")
	public boolean isDashboard(HttpServletRequest request) {
		return request.getRequestURI().startsWith("/dashboard");
	}

	// Indique si on est sur la section profils
	@ModelAttribute("isProfiles")
	public boolean isProfiles(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return uri.startsWith("/profiles") || uri.startsWith("/rubrics");
	}

	// Indique si on est sur la section utilisateurs
	@ModelAttribute("isUsers")
	public boolean isUsers(HttpServletRequest request) {
		String uri = request.getRequestURI();
		// /users mais pas /users/register (qui est public)
		return uri.startsWith("/users") && !uri.contains("/register");
	}

	// Fournit l'annee courante pour le footer
	@ModelAttribute("currentYear")
	public int currentYear() {
		return java.time.Year.now().getValue();
	}
}