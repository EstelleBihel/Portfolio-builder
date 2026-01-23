package alt.portfolio.builder.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.UserRepository;

//Controleur pour l'authentification US-001 : Inscription
@Controller
public class AuthController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// US-001 : Afficher le formulaire d'inscription
	@GetMapping("/users/register")
	public ModelAndView showRegisterForm() {
		ModelAndView mv = new ModelAndView("users/register");
		// Passer des valeurs vides pour eviter les erreurs Mustache
		mv.addObject("firstname", "");
		mv.addObject("lastname", "");
		mv.addObject("username", "");
		mv.addObject("email", "");
		return mv;
	}

	// US-001 : Traiter l'inscription
	@PostMapping("/users/register")
	public String processRegistration(@RequestParam String firstname, @RequestParam String lastname,
			@RequestParam String username, @RequestParam String email, @RequestParam String password,
			@RequestParam String confirmPassword, RedirectAttributes redirectAttributes) {

		// Validation des champs obligatoires
		if (firstname == null || firstname.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Le prenom est obligatoire.");
			return "redirect:/users/register";
		}
		if (lastname == null || lastname.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Le nom est obligatoire.");
			return "redirect:/users/register";
		}
		if (username == null || username.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Le nom d'utilisateur est obligatoire.");
			return "redirect:/users/register";
		}
		if (email == null || email.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "L'email est obligatoire.");
			return "redirect:/users/register";
		}
		if (password == null || password.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Le mot de passe est obligatoire.");
			return "redirect:/users/register";
		}

		// Nettoyer les donnees
		firstname = firstname.trim();
		lastname = lastname.trim();
		username = username.trim().toLowerCase();
		email = email.trim().toLowerCase();

		// Validation du username
		if (username.length() < 3) {
			redirectAttributes.addFlashAttribute("error", "Le nom d'utilisateur doit contenir au moins 3 caracteres.");
			redirectAttributes.addFlashAttribute("firstname", firstname);
			redirectAttributes.addFlashAttribute("lastname", lastname);
			redirectAttributes.addFlashAttribute("email", email);
			return "redirect:/users/register";
		}
		if (username.contains(" ")) {
			redirectAttributes.addFlashAttribute("error", "Le nom d'utilisateur ne peut pas contenir d'espaces.");
			redirectAttributes.addFlashAttribute("firstname", firstname);
			redirectAttributes.addFlashAttribute("lastname", lastname);
			redirectAttributes.addFlashAttribute("email", email);
			return "redirect:/users/register";
		}

		// Validation de l'email
		if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
			redirectAttributes.addFlashAttribute("error", "L'adresse email n'est pas valide.");
			redirectAttributes.addFlashAttribute("firstname", firstname);
			redirectAttributes.addFlashAttribute("lastname", lastname);
			redirectAttributes.addFlashAttribute("username", username);
			return "redirect:/users/register";
		}

		// Validation du mot de passe
		if (password.length() < 6) {
			redirectAttributes.addFlashAttribute("error", "Le mot de passe doit contenir au moins 6 caracteres.");
			redirectAttributes.addFlashAttribute("firstname", firstname);
			redirectAttributes.addFlashAttribute("lastname", lastname);
			redirectAttributes.addFlashAttribute("username", username);
			redirectAttributes.addFlashAttribute("email", email);
			return "redirect:/users/register";
		}

		// Verifier que les mots de passe correspondent
		if (!password.equals(confirmPassword)) {
			redirectAttributes.addFlashAttribute("error", "Les mots de passe ne correspondent pas.");
			redirectAttributes.addFlashAttribute("firstname", firstname);
			redirectAttributes.addFlashAttribute("lastname", lastname);
			redirectAttributes.addFlashAttribute("username", username);
			redirectAttributes.addFlashAttribute("email", email);
			return "redirect:/users/register";
		}

		// Verifier si le username existe deja
		User existingUser = userRepository.findByUsername(username);
		if (existingUser != null) {
			redirectAttributes.addFlashAttribute("error", "Ce nom d'utilisateur est deja pris.");
			redirectAttributes.addFlashAttribute("firstname", firstname);
			redirectAttributes.addFlashAttribute("lastname", lastname);
			redirectAttributes.addFlashAttribute("email", email);
			return "redirect:/users/register";
		}

		// Verifier si l'email existe deja
		User existingEmail = userRepository.findByEmail(email);
		if (existingEmail != null) {
			redirectAttributes.addFlashAttribute("error", "Cette adresse email est deja utilisee.");
			redirectAttributes.addFlashAttribute("firstname", firstname);
			redirectAttributes.addFlashAttribute("lastname", lastname);
			redirectAttributes.addFlashAttribute("username", username);
			return "redirect:/users/register";
		}

		try {
			// Creer le nouvel utilisateur
			User newUser = new User();
			newUser.setFirstname(firstname);
			newUser.setLastname(lastname);
			newUser.setUsername(username);
			newUser.setEmail(email);
			newUser.setPassword(passwordEncoder.encode(password));
			newUser.setRole("USER");

			userRepository.save(newUser);

			redirectAttributes.addFlashAttribute("success",
					"Votre compte a ete cree avec succes ! Vous pouvez maintenant vous connecter.");
			return "redirect:/login";

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erreur lors de la creation du compte : " + e.getMessage());
			redirectAttributes.addFlashAttribute("firstname", firstname);
			redirectAttributes.addFlashAttribute("lastname", lastname);
			redirectAttributes.addFlashAttribute("username", username);
			redirectAttributes.addFlashAttribute("email", email);
			return "redirect:/users/register";
		}
	}
}