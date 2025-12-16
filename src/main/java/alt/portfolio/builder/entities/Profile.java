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

	// cascadeType.REMOVE pour supprimer les profils lorsque l'utilisateur est
	// supprimé
	@ManyToOne(optional = false, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private User owner;

	public void setOwner(User user) {
		this.owner = user;
	}

	// "mappedBy = profile" signifie que c'est le champ 'profile' dans l'entité
	// Rubric qui porte la clé étrangère.
	// Ajout de la relation OneToMany vers Rubric => Cela indique que la relation
	// est gérée par l'entité Rubric.
	@OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude // Important pour éviter boucle infinie
	private List<Rubric> rubrics;
}