package alt.portfolio.builder.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

	// Retrouver une localisation par son nom (pour éviter les doublons + migration)
	Location findByName(String name);
}