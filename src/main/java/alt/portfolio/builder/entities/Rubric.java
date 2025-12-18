package alt.portfolio.builder.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
public class Rubric {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id; // Renommé 'id' (plus simple pour le HTML)

	private String name;

	// CORRECTION : Le champ s'appelle displayOrder pour que Lombok génère
	// setDisplayOrder()
	@Column(name = "display_order")
	private Integer displayOrder;

	@ManyToOne(optional = false)
	@JoinColumn(name = "profile_id")
	@ToString.Exclude
	private Profile profile;
}