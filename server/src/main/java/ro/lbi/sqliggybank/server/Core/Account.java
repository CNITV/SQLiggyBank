package ro.lbi.sqliggybank.server.Core;

/**
 * A class that represents an SQLiggyBank account.
 *
 * A mere container for a username and password and nothing more. Constructed to assist in deserialization by Jackson.
 *
 * @author StormFireFox1
 * @since 2018-11-24
 */
public class Account {

	private String username;
	private String password;

	public Account() {
	}

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

	/**
	 * Gets the account's username.
	 *
	 * @return The account's username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Changes the account's username to specific value.
	 *
	 * @param userName The new username. Must not be empty.
	 */
	public void changeUserName(String userName) {
		if (userName.equals("")) {
			throw new IllegalArgumentException("Username must not be empty!");
		}
		this.username = userName;
	}

	/**
	 * Gets the account's password.
	 *
	 * @return The account's password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Changes the account's password to specific value.
	 *
	 * @param password The new username. Must not be empty.
	 */
	public void changePassword(String password) {
		if (password.equals("")) {
			throw new IllegalArgumentException("Password must not be empty!");
		}
		this.password = password;
	}
}
