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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idRubric;

    private String name;

    @Column(name = "display_order")
    private Integer order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profile_id")
    @ToString.Exclude // Important pour éviter boucle infinie avec Lombok
    private Profile profile;

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}