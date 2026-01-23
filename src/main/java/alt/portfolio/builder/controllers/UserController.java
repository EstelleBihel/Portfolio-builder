package alt.portfolio.builder.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.services.UserService;

//Controleur pour la gestion des utilisateurs (admin) Liste des utilisateurs et actions admin
@Controller
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	// Afficher la liste de tous les utilisateurs
	@GetMapping(path = { "", "/" })
	public ModelAndView index() {
		return new ModelAndView("users/index", "users", userService.getUsers());
	}

	// Supprimer un utilisateur (admin)
	@PostMapping("/{id}/delete")
	public String deleteUser(@PathVariable UUID id, Authentication auth, RedirectAttributes redirectAttributes) {
		User currentUser = (User) auth.getPrincipal();

		// Empecher de supprimer son propre compte via cette route
		if (currentUser.getId().equals(id)) {
			redirectAttributes.addFlashAttribute("error", "Utilisez la page 'Mon compte' pour supprimer votre compte.");
			return "redirect:/users";
		}

		try {
			userService.deleteUser(id);
			redirectAttributes.addFlashAttribute("success", "L'utilisateur a ete supprime.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
		}

		return "redirect:/users";
	}
}