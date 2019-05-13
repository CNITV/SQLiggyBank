package ro.lbi.sqliggybank.server.Core;

import javax.inject.Named;
import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.*;

/**
 * User is a class that represents a user of SQLiggyBank. It contains a username, password and personal identifiable
 * information that might be needed for a user.
 * <p>
 * Most of the credentials kept are relatively standard and not completely privacy-invasive.
 *
 * @author StormFireFox1
 * @since 2018-11-24
 */
@Entity
@Table(name = "users")
@NamedQueries(
		{
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.User.findAll",
						query = "SELECT u FROM User u"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.User.findByID",
						query = "SELECT u FROM User u WHERE u.uuid = :uuid"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.User.findByUsername",
						query = "SELECT u FROM User u WHERE u.username = :username"
				),
		})
public class User {
	/**
	 * The UUID of the user.
	 */
	@Id
	private UUID uuid;

	/**
	 * The username of the user.
	 */
	@Column(name = "username", nullable = false)
	private String username;

	/**
	 * The password of the user.
	 */
	@Column(name = "password", nullable = false)
	private String password;

	/**
	 * The first name of the user.
	 */
	@Column(name = "first_name")
	private String first_name;

	/**
	 * The last name of the user.
	 */
	@Column(name = "last_name")
	private String last_name;

	/**
	 * The e-mail of the user.
	 */
	@Column(name = "email")
	private String email;

	/**
	 * Default constructor, used by Hibernate to enable useful casting for persistence, among other things.
	 */
	public User() {

	}

	/**
	 * The main constructor for User.
	 *
	 * @param uuid The UUID of the user.
	 * @param username The username of the user.
	 * @param password The password of the user.
	 * @param first_name The first name of the user.
	 * @param last_name The last name of the user.
	 * @param email The e-mail fo the user.
	 */
	public User(UUID uuid, String username, String password, String first_name, String last_name, String email) {
		if (uuid == null) {
			throw new IllegalArgumentException("Invalid UUID!");
		} else if (username == null || username.equals("")) {
			throw new IllegalArgumentException("Username must exist and cannot be empty!");
		} else if (password == null || password.equals("")) {
			throw new IllegalArgumentException("Password must exist and cannot be empty! What kind of person are you, without a password?");
		}
		this.uuid = uuid;
		this.username = username;
		this.password = password;
		this.first_name = first_name;
		this.last_name = last_name;
		this.email = email;
	}

	/**
	 * Gets the UUID of the user.
	 *
	 * @return The UUID of the user.
	 */
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * Gets the username of the user.
	 *
	 * @return The username of the user.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Gets the password of the user.
	 *
	 * @return The password of the user.
	 */
	@JsonIgnore
	public String getPassword() {
		return password;
	}

	/**
	 * Gets the first name of the user.
	 *
	 * @return The first name of the user.
	 */
	public String getFirst_name() {
		return first_name;
	}

	/**
	 * Gets the last name of the user.
	 *
	 * @return The last name of the user.
	 */
	public String getLast_name() {
		return last_name;
	}

	/**
	 * Gets the e-mail of the user.
	 *
	 * @return The e-mail of the user.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets a new UUID for the existing user.
	 *
	 * WARNING: Do not call this method on a DAO-provided user, this will throw exceptions 100% of the time.
	 *
	 * @param uuid The new UUID for the existing user.
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Sets a new username for the existing user.
	 *
	 * WARNING: Calling this method on a DAO-provided user will save that change in the database. Be careful while
	 * using setter methods on these objects. Consider cloning them with a constructor instead.
	 *
	 * @param username The new username for the existing user.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Sets a new password for the existing user.
	 *
	 * WARNING: Calling this method on a DAO-provided user will save that change in the database. Be careful while
	 * using setter methods on these objects. Consider cloning them with a constructor instead.
	 *
	 * @param password The new password for the existing user.
	 */
	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets a new first name for the existing user.
	 *
	 * WARNING: Calling this method on a DAO-provided user will save that change in the database. Be careful while
	 * using setter methods on these objects. Consider cloning them with a constructor instead.
	 *
	 * @param first_name The new first name for the existing user.
	 */
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	/**
	 * Sets a new last name for the existing user.
	 *
	 * WARNING: Calling this method on a DAO-provided user will save that change in the database. Be careful while
	 * using setter methods on these objects. Consider cloning them with a constructor instead.
	 *
	 * @param last_name The new last name for the existing user.
	 */
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	/**
	 * Sets a new e-mail for the existing user.
	 *
	 * WARNING: Calling this method on a DAO-provided user will save that change in the database. Be careful while
	 * using setter methods on these objects. Consider cloning them with a constructor instead.
	 *
	 * @param email The new e-mail for the existing user.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof User)) return false;
		User user = (User) o;
		return Objects.equals(uuid, user.uuid) &&
				Objects.equals(username, user.username) &&
				Objects.equals(password, user.password) &&
				Objects.equals(first_name, user.first_name) &&
				Objects.equals(last_name, user.last_name) &&
				Objects.equals(email, user.email);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, username, password, first_name, last_name, email);
	}
}
