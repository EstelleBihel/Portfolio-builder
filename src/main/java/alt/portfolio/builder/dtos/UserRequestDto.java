package alt.portfolio.builder.dtos;

import alt.portfolio.builder.entities.User;
import lombok.Data;



@Data
public class UserRequestDto {
	private String username;
	private String lastname;
	private String firstname;
	private String email;
		
	public User toUser(User user) {
		user.setUsername(username);
		user.setFirstname(firstname);
		user.setLastname(lastname);
		user.setEmail(email);
		return user;
	}

}
