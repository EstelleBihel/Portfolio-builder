package alt.portfolio.builder.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.dtos.UserRequestDto;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
// recuperer la liste des utilisateurs userRepository= c'est le repository qui requete la BDD
	public List<User> getUsers(){
		return userRepository.findAll();
	}
	
// recuperer un utilisateur par son id
	public User getUserById(String id) {
		return userRepository.findById(java.util.UUID.fromString(id)).orElse(null);
	}

// creer un nouvel utilisateur
	public User createUser(UserRequestDto userRequest) {
		 if (userRepository.existsByUsername(userRequest.getUsername())) {
		        throw new IllegalArgumentException("Un utilisateur avec ce username existe déjà");
		    }
		User user=userRequest.toUser(new User());		
		return userRepository.save(user);
	}
//supprimer un utilisateur
	public void deleteUser(String id) {
		userRepository.deleteById(java.util.UUID.fromString(id));
	}
	
// mettre a jour un utilisateur
	public User updateUser(String id, UserRequestDto userRequest) {
		User existingUser = getUserById(id);
	    if (existingUser == null) {
	        throw new IllegalArgumentException("Utilisateur non trouvé");
	    }
	    userRequest.toUser(existingUser);
	    return userRepository.save(existingUser);
	}
	
}
