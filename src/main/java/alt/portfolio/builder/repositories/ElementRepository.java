package alt.portfolio.builder.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Element;

//Repository pour l'acces aux donnees des elements
@Repository
public interface ElementRepository extends JpaRepository<Element, UUID> {

	// Trouver tous les elements d'une rubrique, tries par ordre d'affichage
	List<Element> findByRubricIdOrderByDisplayOrderAsc(UUID rubricId);

	// Compter les elements d'une rubrique
	long countByRubricId(UUID rubricId);

	// Trouver l'element avec l'ordre d'affichage le plus élevé pour une rubrique
	Element findFirstByRubricIdOrderByDisplayOrderDesc(UUID rubricId);
}