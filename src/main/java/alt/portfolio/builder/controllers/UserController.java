//controller définit la route pour gerer les utilisateurs

package alt.portfolio.builder.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import alt.portfolio.builder.dtos.UserRequestDto;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.services.UserService;
import org.springframework.web.bind.annotation.PathVariable;

@RequestMapping("users")
@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
// afficher la liste des utilisateurs
	@GetMapping(path= {"", "/"})
	public ModelAndView index() {
		return new ModelAndView("/users/index", "users", userService.getUsers());
	}
// afficher le formulaire de creation d'un utilisateur	
	@GetMapping("/create")
	public String create(ModelMap model) {
		model.addAttribute("user", new User());
		return "/users/userForm";
	}
// traiter le formulaire de creation d'un utilisateur	
	@PostMapping("/create")
	public RedirectView createUser(@ModelAttribute UserRequestDto createUser) {
		userService.createUser(createUser);
		return new RedirectView("/users");
	}
	
	@GetMapping("/{id}")
	public ModelAndView show(@PathVariable String id) {
	    User user = userService.getUserById(id);
	    ModelAndView mv = new ModelAndView("/users/show");
	    mv.addObject("user", user);
	    return mv;
	}

	@GetMapping("/user/{id}/delete")
	public RedirectView deleteUser(@PathVariable String id) {
		userService.deleteUser(id);
		return new RedirectView("/users");
	}
	
	@GetMapping("/user/{id}/edit")
	public ModelAndView editUser(@PathVariable String id) {
	    User user = userService.getUserById(id);
	    ModelAndView mv = new ModelAndView("/users/userForm");
	    mv.addObject("user", user);
	    return mv;
	}
}
