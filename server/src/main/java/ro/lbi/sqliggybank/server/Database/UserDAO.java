package ro.lbi.sqliggybank.server.Database;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import ro.lbi.sqliggybank.server.Core.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDAO extends AbstractDAO<User> {
	public UserDAO(SessionFactory factory) {
		super(factory);
	}

	public Optional<User> findByID(UUID id) {
		return Optional.ofNullable(get(id));
	}

	public Optional<User> findByUsername(String username) {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.User.findByUsername");
		query.setParameter("username", username);
		User user = (User) query.getSingleResult();
		return Optional.of(user);
	}

	public void create(User user) {
		persist(user);
	}

	public void delete(User user) {
		currentSession().delete(user);
	}

	public void update(User user) {
		currentSession().update(user);
	}

	@SuppressWarnings("unchecked")
	public List<User> findAll() {
		return list((Query<User>) namedQuery("ro.lbi.sqliggybank.server.Core.User.findAll"));
	}
}
