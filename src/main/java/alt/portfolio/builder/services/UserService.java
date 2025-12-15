package alt.portfolio.builder.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.dtos.UserRequestDto;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.UserRepository;


@Service
public class UserService  {
	
	@Autowired
	private UserRepositories userRepositories;
	
	public List<User> getUsers(){
		return userRepositories.findAll();
	}
	
	public User createUser(userRequestDto userRequest) {
		User user = userRequest.toUser(new User());
		return userRepositories.save(user);
	}
   public User getUserById(UUID id) {
       return userRepositories.findById(id)
           .orElseThrow(() -> new RuntimeException("Utilisateur introuvable: " + id));
   }
   public void deleteUser(UUID id) {
       userRepositories.deleteById(id);
   }
}
