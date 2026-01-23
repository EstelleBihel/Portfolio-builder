package alt.portfolio.builder.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

//Entite Element - Represente un element dans une rubrique Exemple : une formation, une experience, une competence, etc.
@Entity
@Table(name = "elements")
public class Element {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(length = 100)
	private String subtitle;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(length = 50)
	private String startDate;

	@Column(length = 50)
	private String endDate;

	@Column(length = 255)
	private String location;

	@Column(length = 500)
	private String link;

	@Column(nullable = false)
	private Integer displayOrder = 0;

	@ManyToOne
	@JoinColumn(name = "rubric_id", nullable = false)
	private Rubric rubric;

	// Constructeurs
	public Element() {
	}

	public Element(String title, Rubric rubric) {
		this.title = title;
		this.rubric = rubric;
	}

	// Getters et Setters
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Rubric getRubric() {
		return rubric;
	}

	public void setRubric(Rubric rubric) {
		this.rubric = rubric;
	}

	// Helper pour afficher la periode
	public String getPeriod() {
		if (startDate == null && endDate == null) {
			return null;
		}
		if (startDate != null && endDate != null) {
			return startDate + " - " + endDate;
		}
		if (startDate != null) {
			return "Depuis " + startDate;
		}
		return "Jusqu'a " + endDate;
	}

	// Helper pour verifier si l'element a une periode
	public boolean hasPeriod() {
		return startDate != null || endDate != null;
	}

	// Helper pour verifier si l'element a un lien
	public boolean hasLink() {
		return link != null && !link.isEmpty();
	}
}