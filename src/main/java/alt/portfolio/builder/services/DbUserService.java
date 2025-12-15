@Service
public class DbUserService implements UserDetailsService {

	@Autowired
	private UserRepository uRepo;

	@Autowired
	private PasswordEncoder pEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> optUser = uRepo.findByUsername(username);
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
		user.setEmail(username + ".mail.fr");
		user.setPassword(password);
		encodePassword(user);
		return uRepo.save(user);
	}
}


