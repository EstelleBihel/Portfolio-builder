package alt.portfolio.builder.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.UserRepository;
import alt.portfolio.builder.services.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//Controleur pour la gestion du compte utilisateur US-004 : Voir mon profil utilisateur US-005 : Modifier mon profil utilisateur
@Controller
public class AccountController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ProfileService profileService;

	// US-004 : Voir mon profil utilisateur
	@GetMapping("/users/{id}")
	public ModelAndView showAccount(@PathVariable UUID id, Authentication auth) {
		User currentUser = (User) auth.getPrincipal();

		// Verifier que l'utilisateur consulte son propre compte
		if (!currentUser.getId().equals(id)) {
			return new ModelAndView("redirect:/users/" + currentUser.getId());
		}

		// Recharger l'utilisateur depuis la base pour avoir les donnees a jour
		User user = userRepository.findById(id).orElse(currentUser);

		ModelAndView mv = new ModelAndView("users/show");
		mv.addObject("user", user);
		mv.addObject("profileCount", profileService.countProfilesByUser(id));
		mv.addObject("publishedCount", profileService.countPublishedProfilesByUser(id));
		return mv;
	}

	// US-005 : Afficher le formulaire de modification
	@GetMapping("/users/{id}/edit")
	public ModelAndView editAccount(@PathVariable UUID id, Authentication auth) {
		User currentUser = (User) auth.getPrincipal();

		if (!currentUser.getId().equals(id)) {
			return new ModelAndView("redirect:/users/" + currentUser.getId() + "/edit");
		}

		User user = userRepository.findById(id).orElse(currentUser);

		ModelAndView mv = new ModelAndView("users/edit");
		mv.addObject("user", user);
		return mv;
	}

	// US-005 : Traiter la modification du compte
	@PostMapping("/users/{id}/update")
	public String updateAccount(@PathVariable UUID id, @RequestParam String firstname, @RequestParam String lastname,
			@RequestParam String email, Authentication auth, RedirectAttributes redirectAttributes) {

		User currentUser = (User) auth.getPrincipal();

		if (!currentUser.getId().equals(id)) {
			redirectAttributes.addFlashAttribute("error", "Vous ne pouvez pas modifier un autre compte.");
			return "redirect:/users/" + currentUser.getId();
		}

		// Validation
		if (firstname == null || firstname.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Le prenom est obligatoire.");
			return "redirect:/users/" + id + "/edit";
		}
		if (lastname == null || lastname.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Le nom est obligatoire.");
			return "redirect:/users/" + id + "/edit";
		}
		if (email == null || email.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "L'email est obligatoire.");
			return "redirect:/users/" + id + "/edit";
		}

		// Nettoyer
		firstname = firstname.trim();
		lastname = lastname.trim();
		email = email.trim().toLowerCase();

		// Verifier le format email
		if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
			redirectAttributes.addFlashAttribute("error", "L'adresse email n'est pas valide.");
			return "redirect:/users/" + id + "/edit";
		}

		// Verifier si l'email est deja utilise par un autre utilisateur
		User existingEmail = userRepository.findByEmail(email);
		if (existingEmail != null && !existingEmail.getId().equals(id)) {
			redirectAttributes.addFlashAttribute("error", "Cette adresse email est deja utilisee.");
			return "redirect:/users/" + id + "/edit";
		}

		try {
			User user = userRepository.findById(id).orElseThrow();
			user.setFirstname(firstname);
			user.setLastname(lastname);
			user.setEmail(email);
			userRepository.save(user);

			redirectAttributes.addFlashAttribute("success", "Vos informations ont ete mises a jour.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise a jour : " + e.getMessage());
		}

		return "redirect:/users/" + id;
	}

	// US-006 - Afficher le formulaire de changement de mot de passe
	@GetMapping("/users/{id}/password")
	public ModelAndView showPasswordForm(@PathVariable UUID id, Authentication auth) {
		User currentUser = (User) auth.getPrincipal();

		if (!currentUser.getId().equals(id)) {
			return new ModelAndView("redirect:/users/" + currentUser.getId() + "/password");
		}

		User user = userRepository.findById(id).orElse(currentUser);

		ModelAndView mv = new ModelAndView("users/password");
		mv.addObject("user", user);
		return mv;
	}

	/**
	 * Traiter le changement de mot de passe
	 */
	@PostMapping("/users/{id}/password")
	public String changePassword(@PathVariable UUID id, @RequestParam String currentPassword,
			@RequestParam String newPassword, @RequestParam String confirmPassword, Authentication auth,
			RedirectAttributes redirectAttributes) {

		User currentUser = (User) auth.getPrincipal();

		if (!currentUser.getId().equals(id)) {
			redirectAttributes.addFlashAttribute("error", "Vous ne pouvez pas modifier un autre compte.");
			return "redirect:/users/" + currentUser.getId();
		}

		// Verifier le mot de passe actuel
		User user = userRepository.findById(id).orElseThrow();
		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			redirectAttributes.addFlashAttribute("error", "Le mot de passe actuel est incorrect.");
			return "redirect:/users/" + id + "/password";
		}

		// Validation du nouveau mot de passe
		if (newPassword == null || newPassword.length() < 6) {
			redirectAttributes.addFlashAttribute("error",
					"Le nouveau mot de passe doit contenir au moins 6 caracteres.");
			return "redirect:/users/" + id + "/password";
		}

		if (!newPassword.equals(confirmPassword)) {
			redirectAttributes.addFlashAttribute("error", "Les nouveaux mots de passe ne correspondent pas.");
			return "redirect:/users/" + id + "/password";
		}

		try {
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);

			redirectAttributes.addFlashAttribute("success", "Votre mot de passe a ete change avec succes.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error",
					"Erreur lors du changement de mot de passe : " + e.getMessage());
		}

		return "redirect:/users/" + id;
	}

	// Supprimer son propre compte
	@PostMapping("/users/{id}/delete-account")
	public String deleteAccount(@PathVariable UUID id, Authentication auth, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {

		User currentUser = (User) auth.getPrincipal();

		if (!currentUser.getId().equals(id)) {
			redirectAttributes.addFlashAttribute("error", "Vous ne pouvez pas supprimer un autre compte.");
			return "redirect:/users/" + currentUser.getId();
		}

		try {
			// Supprimer l'utilisateur (les profils seront supprimes en cascade)
			userRepository.deleteById(id);

			// Deconnecter l'utilisateur
			new SecurityContextLogoutHandler().logout(request, response, auth);
			SecurityContextHolder.clearContext();

			return "redirect:/login?accountDeleted=true";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error",
					"Erreur lors de la suppression du compte : " + e.getMessage());
			return "redirect:/users/" + id + "/edit";
		}
	}
}