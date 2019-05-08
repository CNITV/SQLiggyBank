package ro.lbi.sqliggybank.client.backend.user;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * This class represents a user. A new user is automatically created by GSON when the said user logs in.
 *
 * <p>
 * WARNING: A User instance is not supposed to be created directly by the programmer.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-12-08
 */
@Getter
public class User {

	/**
	 * The JWT of the user received when logging in.
	 */
	@Setter
	private String JWT;

	/**
	 * The UUID of the user.
	 */
	private UUID uuid;

	/**
	 * The username of the user.
	 */
	private String username;

	/**
	 * The password of the user.
	 */
	private String password;

	/**
	 * The first name of the user.
	 */
	private String first_name;

	/**
	 * The last name of the user.
	 */
	private String last_name;

	/**
	 * The e-mail of the user.
	 */
	private String email;

	@Override
	public String toString() {
		return "UUID: " + uuid + "\n" +
				"Username: " + username + "\n" +
				"Password: " + password + "\n" +
				"First name: " + first_name + "\n" +
				"Last name: " + last_name + "\n" +
				"Email: " + email + "\n" +
				"JWT: " + JWT + "\n";
	}
}

