package alt.portfolio.builder.entities;

import java.util.UUID;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Getter
@Setter
@Entity
@Data
public class Profile {
	@Id
	private UUID id= UUID.randomUUID();
	
	@Column(length = 65)
	private String name;
	
	@Column(length = 10400)
	private String description;
	
	@ManyToOne (optional = false)
	private User owner;

	public void setOwner(User user) {
		this.owner = user;
	}
}