package alt.portfolio.builder.entities;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Data
public class Profile {

	@Id
	private UUID id = UUID.randomUUID();

	@Column(length = 65)
	private String name;

	@Column(length = 10400)
	private String description;

	@Column(nullable = true)
	private Boolean isPublished = false;

	@Column(nullable = true, unique = true)
	private String slug;

	// CORRECTION : Pas de cascade sur la relation vers User
	// On ne veut PAS supprimer l'utilisateur quand on supprime un profil !
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private User owner;

	// Les rubriques sont supprimees en cascade quand le profil est supprime
	@OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private List<Rubric> rubrics;

	// Getters et Setters explicites (en plus de Lombok pour compatibilite)

	public Boolean isPublished() {
		return isPublished;
	}

	public void setPublished(Boolean published) {
		isPublished = published;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public void setOwner(User user) {
		this.owner = user;
	}

	public User getOwner() {
		return owner;
	}

	// Methode utilitaire
	public int getRubricsCount() {
		return rubrics != null ? rubrics.size() : 0;
	}
}