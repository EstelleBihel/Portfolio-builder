package alt.portfolio.builder.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	// public optional ser findByUsername(String username);

	// @Query ("select u from User u where u.email like :pattern")
	// public List<User> searchBy(String pattern);

	User findByUsername(String username);

	// Methode pour l'authentification (username OU email)
	Optional<User> findByUsernameOrEmail(String username, String email);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}