package alt.portfolio.builder.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

	// Retrouver une catégorie par son nom (utile pour la migration et le
	// formulaire)
	Category findByName(String name);

	// Lister toutes les catégories triées par nom (pour le dropdown)
	List<Category> findAllByOrderByNameAsc();
}
