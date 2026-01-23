package alt.portfolio.builder.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User implements UserDetails {

	@Id
	private UUID id = UUID.randomUUID();

	@Column(length = 45, nullable = false)
	private String firstname = "";

	@Column(length = 45, nullable = false)
	private String lastname = "";

	@Column(length = 45, nullable = false, unique = true)
	private String username = "";

	@Column(length = 255, nullable = false)
	private String password = "";

	@Column(length = 150, nullable = false, unique = true)
	private String email = "";

	@Column(length = 50, nullable = false)
	private String role = "ROLE_USER";

	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Profile> profiles = new ArrayList<>();

	public void addProfile(Profile profile) {
		this.profiles.add(profile);
		profile.setOwner(this);
	}

	// Methode pour obtenir les initiales
	public String getInitials() {
		String initials = "";
		if (firstname != null && !firstname.isEmpty()) {
			initials += firstname.substring(0, 1).toUpperCase();
		}
		if (lastname != null && !lastname.isEmpty()) {
			initials += lastname.substring(0, 1).toUpperCase();
		}
		if (initials.isEmpty() && username != null && !username.isEmpty()) {
			initials = username.substring(0, 1).toUpperCase();
		}
		return initials;
	}

	// Verifie si l'utilisateur est admin
	public boolean isAdmin() {
		return "ROLE_ADMIN".equals(role);
	}

	// Retourne les autorites (roles) de l'utilisateur pour Spring Security
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(role));
		return authorities;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

}