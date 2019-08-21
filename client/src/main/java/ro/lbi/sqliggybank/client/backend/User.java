package ro.lbi.sqliggybank.client.backend;

import java.util.UUID;

/**
 * This class represents a user. A new user is automatically created by GSON when the said user logs in.
 * <p>
 * WARNING: A User instance is not supposed to be created directly by the programmer.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-12-08
 */
public class User {

	/**
	 * The JWT of the user received when logging in.
	 */
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

	public String getFirst_name() {
		return first_name != null ? first_name : "";
	}

	public String getLast_name() {
		return last_name != null ? last_name : "";
	}

	public String getEmail() {
		return email != null ? email : "";
	}

	@Override
	public String toString() {
		return "Username: " + username + '\n' +
				"First name: " + (first_name == null ? " - " : first_name) + '\n' +
				"Last name: " + (last_name == null ? " - " : last_name) + '\n' +
				"E-mail: " + (email == null ? " - " : email) + '\n';
	}

	public String getJWT() {
		return this.JWT;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setJWT(String JWT) {
		this.JWT = JWT;
	}
}

