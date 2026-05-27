package alt.portfolio.builder.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entité Category (EPIC évolution - MCD) Catégorise les rubriques (FORMATION,
 * EXPERIENCE, PROJET...). Relation : Category (1) ---- (0,n) Rubric
 *
 * hasDates : indique si les éléments de cette catégorie affichent des dates
 * hasLink : indique si les éléments de cette catégorie affichent un lien
 */
@Entity
@Table(name = "category")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(name = "has_dates", nullable = false)
	private Boolean hasDates = false;

	@Column(name = "has_link", nullable = false)
	private Boolean hasLink = false;

	// === GETTERS / SETTERS ===

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getHasDates() {
		return hasDates;
	}

	public void setHasDates(Boolean hasDates) {
		this.hasDates = hasDates;
	}

	public Boolean getHasLink() {
		return hasLink;
	}

	public void setHasLink(Boolean hasLink) {
		this.hasLink = hasLink;
	}
}