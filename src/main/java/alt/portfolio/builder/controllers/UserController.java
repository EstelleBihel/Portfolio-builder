//controller définit la route pour gerer les utilisateurs

package alt.portfolio.builder.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import alt.portfolio.builder.dtos.UserRequestDto;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.services.DbUserService;
import alt.portfolio.builder.services.UserService;

@Controller
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private DbUserService dbUserService;
	
	//on recupere les info de user service on les passe a users et on les passe dans l'index
    @GetMapping(path= {"","/"})
    public ModelAndView index() {
        return new ModelAndView("/users/index","users",userService.getUsers());
    }
	
    // Creer un utilisateur via l'URL /users/register/{username}/{password}
	@GetMapping("/register/{username}/{password}")
	@ResponseBody
	public User createUser(@PathVariable String username, @PathVariable String password) {
		return dbUserService.createUser(username, password);
	}

    // Afficher le formulaire de creation d'un utilisateur	
	@GetMapping("/create")
	public String create(ModelMap model) {
		model.addAttribute("user", new User());
		return "users/userForm";
	}

    // Traiter le formulaire de creation d'un utilisateur	
	@PostMapping("/create")
	public RedirectView createUser(@ModelAttribute UserRequestDto createUser) {
		userService.createUser(createUser);
		return new RedirectView("/users");
	}
	
	// Afficher les details d'un utilisateur
	@GetMapping("/{id}")
	public ModelAndView show(@PathVariable UUID id) {
	    User user = userService.getUserById(id);
	    ModelAndView mv = new ModelAndView("users/show");
	    mv.addObject("user", user);
	    return mv;
	}
	// Supprimer un utilisateur
	@PostMapping("/{id}/delete")
	public RedirectView deleteUser(@PathVariable UUID id) {
	    userService.deleteUser(id);
	    return new RedirectView("/users");
	}
	
	// Mettre a jour/Modifier un utilisateur
	@PostMapping("/{id}/edit")
	public RedirectView updateUser(@PathVariable UUID id, @ModelAttribute UserRequestDto userRequest) {
	    userService.updateUser(id, userRequest);
	    return new RedirectView("/users");
	}
	
	// Dupliquer un utilisateur
	@GetMapping("/{id}/duplicate")
	public ModelAndView duplicateUser(@PathVariable UUID id) {
	    User original = userService.getUserById(id);
	    
	    User duplicate = new User();
	    duplicate.setFirstname(original.getFirstname() + " (copie)");
	    duplicate.setLastname(original.getLastname());
	    duplicate.setUsername(original.getUsername() + "_copy");
	    duplicate.setEmail("");
	    
	    ModelAndView mv = new ModelAndView("users/userForm");
	    mv.addObject("user", duplicate);
	    mv.addObject("isEdit", false);  // Duplication = création
	    return mv;
	}

	// Afficher le formulaire d'edition d'un utilisateur
	@GetMapping("/{id}/edit")
	public ModelAndView editUser(@PathVariable UUID id) {
	    User user = userService.getUserById(id);  // Utilise getUserById au lieu de findById
	    ModelAndView mv = new ModelAndView("users/userForm");
	    mv.addObject("user", user);
	    return mv;
	}
	
	
}