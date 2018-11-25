package ro.lbi.sqliggybank.server.Core;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

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
	@Id
	private UUID uuid;

	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "first_name")
	private String first_name;

	@Column(name = "last_name")
	private String last_name;

	@Column(name = "email")
	private String email;

	public User() {

	}

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

	public UUID getUuid() {
		return uuid;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getFirst_name() {
		return first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public String getEmail() {
		return email;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

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
