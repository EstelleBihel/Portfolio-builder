//controller définit la route pour gerer les utilisateurs

package alt.portfolio.builder.controllers;

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
import alt.portfolio.builder.services.UserService;

@Controller
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping(path = {"","/"})
	@ResponseBody
	public String index() {
		return "ok";
	}
	
	@GetMapping("/register/{username}/{password}")
	@ResponseBody
	public User createUser(@PathVariable String username, @PathVariable String password) {
		return UserService.createUser(username, password);
	}
	
    // Afficher la liste des utilisateurs
	//@GetMapping({"", "/"})
	//public ModelAndView index() {
	//	return new ModelAndView("users/index", "users", userService.getUsers());
	//}

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
	
	@GetMapping("/{id}")
	public ModelAndView show(@PathVariable String id) {
	    User user = userService.getUserById(id);
	    ModelAndView mv = new ModelAndView("users/show");
	    mv.addObject("user", user);
	    return mv;
	}

	@GetMapping("/{id}/delete")
	public RedirectView deleteUser(@PathVariable String id) {
		userService.deleteUser(id);
		return new RedirectView("/users");
	}
	
	@GetMapping("/{id}/edit")
	public ModelAndView editUser(@PathVariable String id) {
	    User user = userService.getUserById(id);
	    ModelAndView mv = new ModelAndView("users/userForm");
	    mv.addObject("user", user);
	    return mv;
	}
	
	
}