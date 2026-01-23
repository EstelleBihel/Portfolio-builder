package alt.portfolio.builder.entities;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "profile")
public class Profile {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(unique = true, length = 150)
	private String slug;

	// === EPIC 4 ===

	// Profil publié en mode Portfolio (vue détaillée avec projets) URL publique :
	// /p/{slug}
	@Column(name = "is_published_portfolio", nullable = false)
	private Boolean isPublishedPortfolio = false;

	// Profil publié en mode CV (vue épurée, focus parcours) URL publique :
	// /cv/{slug}
	@Column(name = "is_published_cv", nullable = false)
	private Boolean isPublishedCv = false;

	// === FIN NOUVEAUX CHAMPS ===

	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("displayOrder ASC")
	private List<Rubric> rubrics;

	// === GETTERS ET SETTERS ===

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public Boolean getIsPublishedPortfolio() {
		return isPublishedPortfolio;
	}

	public void setIsPublishedPortfolio(Boolean isPublishedPortfolio) {
		this.isPublishedPortfolio = isPublishedPortfolio;
	}

	public Boolean getIsPublishedCv() {
		return isPublishedCv;
	}

	public void setIsPublishedCv(Boolean isPublishedCv) {
		this.isPublishedCv = isPublishedCv;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<Rubric> getRubrics() {
		return rubrics;
	}

	public void setRubrics(List<Rubric> rubrics) {
		this.rubrics = rubrics;
	}

	// === MÉTHODES UTILITAIRES POUR MUSTACHE ===

	// Le profil est-il publié (au moins une vue) ?
	public boolean isPublished() {
		return Boolean.TRUE.equals(isPublishedPortfolio) || Boolean.TRUE.equals(isPublishedCv);
	}

	// Le profil est-il en brouillon (aucune vue publiée) ?
	public boolean isDraft() {
		return !isPublished();
	}

	// URL publique du Portfolio
	public String getPortfolioUrl() {
		return "/p/" + slug;
	}

	// URL publique du CV
	public String getCvUrl() {
		return "/cv/" + slug;
	}

	// Le portfolio est-il publié ?
	public boolean isPortfolioPublished() {
		return Boolean.TRUE.equals(isPublishedPortfolio);
	}

	// Le CV est-il publié ?
	public boolean isCvPublished() {
		return Boolean.TRUE.equals(isPublishedCv);
	}
}