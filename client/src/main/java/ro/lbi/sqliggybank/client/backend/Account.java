package ro.lbi.sqliggybank.client.backend;

/**
 * An account for the application. This is just a POJO holding the username and the password.
 *
 * (Credits to @StormFireFox1 for initially designing this)
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-12-06
 */
public class Account {

	/**
	 * The username of the account.
	 */
	private String username;

	/**
	 * The password of the account.
	 */
	private String password;

	/**
	 * Constructs and initializes an Account object.
	 *
	 * @param username The username for the account. Must not be empty.
	 * @param password The password for the account. Must not be empty.
	 */
	public Account(String username, String password) {
		if (username.equals("")) {
			throw new IllegalArgumentException("Username must not be empty!");
		} else if (password.equals("")) {
			throw new IllegalArgumentException("Password must not be empty!");
		}
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}
}
