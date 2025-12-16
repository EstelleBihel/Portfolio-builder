package alt.portfolio.builder.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.UserRepository;

@Service
public class DbUserService implements UserDetailsService {

	@Autowired
	private UserRepository uRepo;

	@Autowired
	private PasswordEncoder pEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> optUser = Optional.ofNullable(uRepo.findByUsername(username));
		return optUser.orElseThrow(() -> new UsernameNotFoundException("Utilisateur inconnu"));
	}

	public void encodePassword(User user) {
		user.setPassword(pEncoder.encode(user.getPassword()));
	}

	public User createUser(String username, String password) {
		User user = new User();
		user.setUsername(username);
		user.setFirstname(username);
		user.setLastname(username);
		user.setEmail(username);
		user.setPassword(password);
		encodePassword(user);
		return uRepo.save(user);
	}
}


