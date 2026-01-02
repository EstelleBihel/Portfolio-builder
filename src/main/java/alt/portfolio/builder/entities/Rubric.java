package alt.portfolio.builder.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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

	@Column(length = 50)
	private String type; // FORMATION, EXPERIENCE, COMPETENCE, PROJET, LANGUE, LOISIR, AUTRE

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

	// Getters et Setters explicites

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
}