package alt.portfolio.builder.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Rubric;

//Repository pour l'acces aux donnees des rubriques
@Repository
public interface RubricRepository extends JpaRepository<Rubric, UUID> {

	// Trouver toutes les rubriques d'un profil, triees par ordre d'affichage
	List<Rubric> findByProfileIdOrderByDisplayOrderAsc(UUID profileId);

	// Trouver les rubriques visibles d'un profil
	List<Rubric> findByProfileIdAndVisibleTrueOrderByDisplayOrderAsc(UUID profileId);

	// Compter les rubriques d'un profil
	long countByProfileId(UUID profileId);

	// Trouver la rubrique avec l'ordre d'affichage le plus eleve pour un profil
	Rubric findFirstByProfileIdOrderByDisplayOrderDesc(UUID profileId);
}