package alt.portfolio.builder.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

	public Profile findProfileById(UUID id);

	// Requête pour récupérer les profils avec leurs propriétaires en une seule
	// @Query("select p from Profile p join fetch p.owner o where o.id = :owner_id")

	// Méthode pour lister les profils d'un utilisateur (US-007)

	List<Profile> findByOwner_Id(UUID ownerId);
}