package ro.lbi.sqliggybank.server.Database;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import ro.lbi.sqliggybank.server.Core.User;

import javax.persistence.NoResultException;
import javax.ws.rs.NotFoundException;
import java.util.List;
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
	public User findByID(UUID id) {
		return get(id);
	}

	/**
	 * Finds user by username.
	 *
	 * @param username The username of the user.
	 * @return Optional of any found user.
	 */
	public User findByUsername(String username) throws NotFoundException {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.User.findByUsername");
		query.setParameter("username", username);
		User user;
		try {
			user = (User) query.getSingleResult();
		} catch (NoResultException e) {
			throw new NotFoundException("No such username.");
		}
		return user;
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
	 *
	 * WARNING: At large scales, this can crash the server. There might be MANY users in the database.
	 *
	 * @return A List of all the users.
	 */
	@SuppressWarnings("unchecked")
	public List<User> findAll() {
		return list((Query<User>) namedQuery("ro.lbi.sqliggybank.server.Core.User.findAll"));
	}
}
