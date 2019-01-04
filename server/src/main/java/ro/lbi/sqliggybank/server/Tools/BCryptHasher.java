package ro.lbi.sqliggybank.server.Tools;

import org.mindrot.jbcrypt.BCrypt;

import java.util.function.Function;

/**
 * BCryptHasher is a class used to hash passwords into the datbaase using the BCrypt algorithm.
 * <p>
 * The BCrypt algorithm randomly generated a new salt for each password it hashes and allows for easy hashing and
 * verification of new passwords. This will be used to encrypt all passwords.
 *
 * @author StormFireFox1
 * @since 2019-01-04
 */
public class BCryptHasher {
	/**
	 * The number of iterations the BCrypt algorithm will pass through.
	 */
	private final int logRounds;

	/**
	 * The constructor for BCryptHasher.
	 *
	 * @param logRounds The number of iterations the BCrypt algorithm will pass through. Preferably set at around 12.
	 */
	public BCryptHasher(int logRounds) {
		this.logRounds = logRounds;
	}

	/**
	 * Hashes a password and returns the hash.
	 *
	 * @param password The password to hash.
	 * @return The hash for the password.
	 */
	public String hash(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt(logRounds));
	}

	/**
	 * Verifies a hash depending on the password inserted.
	 *
	 * @param password The password to verify.
	 * @param hash     The hash to verify on.
	 * @return True or false, depending on whether the password corresponds to the hash or not.
	 */
	public boolean verifyHash(String password, String hash) {
		return BCrypt.checkpw(password, hash);
	}

}
