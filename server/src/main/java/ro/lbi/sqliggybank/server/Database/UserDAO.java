package ro.lbi.sqliggybank.server.Database;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import ro.lbi.sqliggybank.server.Core.User;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * UserDAO is the abstract DAO for the User class.
 * This DAO can find users by ID, username, create, update or delete said users. It can also find all the users, should
 * a list like this be ever needed.
 *
 * @author StormFireFox1
 * @since 2018-11-24
 */
public class UserDAO extends AbstractDAO<User> {
	/**
	 * The constructor for the user DAO.
	 *
	 * @param factory The session factory required.
	 */
	public UserDAO(SessionFactory factory) {
		super(factory);
	}

	/**
	 * Finds user by ID.
	 *
	 * @param id The UUID of the user.
	 * @return Optional of any found user.
	 */
	public Optional<User> findByID(UUID id) {
		return Optional.ofNullable(get(id));
	}

	/**
	 * Finds user by username.
	 *
	 * @param username The username of the user.
	 * @return Optional of any found user.
	 */
	public Optional<User> findByUsername(String username) {
		try {
			Query query = namedQuery("ro.lbi.sqliggybank.server.Core.User.findByUsername");
			query.setParameter("username", username);
			User user = (User) query.getSingleResult();
			return Optional.of(user);
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	/**
	 * Checks if there is any entry in the database which uses a specific e-mail.
	 *
	 * @param email The e-mail to check against in the database.
	 * @return True if a user has been found with this e-mail, false if otherwise.
	 */
	public boolean checkForExistingEmail(String email) {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.User.findByEmail");
		query.setParameter("email", email);
		return (query.uniqueResult() != null);
	}

	/**
	 * Finds user by username in the database and checks whether such user exists.
	 *
	 * @param username The username of the user.
	 * @return True if user exists, false if otherwise.
	 */
	public boolean userExists(String username) {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.User.findByUsername");
		query.setParameter("username", username);
		return (query.uniqueResult() != null);
	}

	/**
	 * Creates a user in the database.
	 *
	 * @param user The user to be created.
	 */
	public void create(User user) {
		persist(user);
	}

	/**
	 * Deletes a user from the database.
	 *
	 * @param user The user to be deleted.
	 */
	public void delete(User user) {
		currentSession().delete(user);
	}

	/**
	 * Updates a user from the database.
	 *
	 * @param user The user to be updated.
	 */
	public void update(User user) {
		currentSession().update(user);
	}

	/**
	 * Finds all users from the database. Equivalent to "SELECT * FROM users".
	 * <p>
	 * WARNING: At large scales, this can crash the server. There might be MANY users in the database.
	 *
	 * @return A List of all the users.
	 */
	@SuppressWarnings("unchecked")
	public List<User> findAll() {
		return list((Query<User>) namedQuery("ro.lbi.sqliggybank.server.Core.User.findAll"));
	}
}
