package alt.portfolio.builder.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.dtos.UserRequestDto;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public List<User> getUsers() {
		return userRepository.findAll();
	}

	public User createUser(UserRequestDto userRequest) {
		User user = userRequest.toUser(new User());
		return userRepository.save(user);
	}

	public User getUserById(UUID id) {
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Utilisateur introuvable: " + id));
	}

	public void deleteUser(UUID id) {
		userRepository.deleteById(id);
	}

	public User updateUser(UUID id, UserRequestDto userRequest) {
		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Utilisateur introuvable: " + id));

		existingUser.setFirstname(userRequest.getFirstname());
		existingUser.setLastname(userRequest.getLastname());
		existingUser.setUsername(userRequest.getUsername());
		existingUser.setEmail(userRequest.getEmail());

		return userRepository.save(existingUser);
	}

	public User findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}
}
