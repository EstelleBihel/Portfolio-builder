package alt.portfolio.builder.entities;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Data
public class Rubric {

	@Id
	private UUID id = UUID.randomUUID();

	@Column(length = 100, nullable = false)
	private String name;

	@Column(length = 5000)
	private String content; // Contenu de la rubrique (texte libre ou JSON)

	@Column(nullable = false)
	private Integer displayOrder = 0;

	@Column(nullable = false)
	private Boolean visible = true;

	// Relation vers le profil parent
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@ToString.Exclude
	private Profile profile;

	// Relation vers la catégorie (évolution MCD)
	// EAGER : la catégorie est toujours affichée (nom + emoji), on la charge avec
	// la rubrique
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "category_id")
	private Category category;

	// Champ transient (pas stocké en BDD, juste pour l'affichage)
	@Transient
	private List<Element> elements;

	// Getter
	public List<Element> getElements() {
		return elements;
	}

	// Setter
	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Boolean isVisible() {
		return visible;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	// =========================================================================
	// MÉTHODES UTILITAIRES POUR MUSTACHE (EPIC 4 - vues publiques)
	// Basées sur la catégorie (relation) au lieu de l'ancien champ type
	// =========================================================================

	private boolean categoryNameEquals(String value) {
		return category != null && value.equals(category.getName());
	}

	public boolean isTypeFormation() {
		return categoryNameEquals("FORMATION");
	}

	public boolean isTypeExperience() {
		return categoryNameEquals("EXPERIENCE");
	}

	public boolean isTypeCompetence() {
		return categoryNameEquals("COMPETENCE");
	}

	public boolean isTypeProjet() {
		return categoryNameEquals("PROJET");
	}

	public boolean isTypeLangue() {
		return categoryNameEquals("LANGUE");
	}

	public boolean isTypeLoisir() {
		return categoryNameEquals("LOISIR");
	}

	public boolean isTypeAutre() {
		return categoryNameEquals("AUTRE");
	}
}