package ro.lbi.sqliggybank.server.Database;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import ro.lbi.sqliggybank.server.Core.Group;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GroupDAO extends AbstractDAO<Group> {
	/**
	 * The constructor for the group DAO.
	 *
	 * @param factory The session factory required. Handed in by Dropwizard.
	 */
	public GroupDAO(SessionFactory factory) {
		super(factory);
	}

	/**
	 * Finds group by ID.
	 *
	 * @param id The UUID of the group.
	 * @return Optional of any found group.
	 */
	public Optional<Group> findByID(UUID id) {
		return Optional.ofNullable(get(id));
	}

	/**
	 * Finds group by name.
	 *
	 * @param name The name of the group.
	 * @return Optional of any found group.
	 */
	public Optional<Group> findByName(String name) {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.Group.findByName");
		query.setParameter("name", name);
		Group group = (Group) query.getSingleResult();
		return Optional.of(group);
	}

	public boolean isUserOwnerOfGroup(String username, String groupName) {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.Group.isUserOwnerOfGroup");
		query.setParameter("username", username);
		query.setParameter("groupName", groupName);
		return !query.getResultList().isEmpty();
	}

	/**
	 * Creates a group in the database.
	 *
	 * @param group The group to be created.
	 */
	public void create(Group group) {
		persist(group);
	}

	/**
	 * Deletes all the users in a group and then deletes the group from the database.
	 *
	 * @param group The group to be deleted.
	 */
	public void delete(Group group) {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.GroupEntry.deleteUsersFromGroup");
		query.setParameter("groupName", group.getName());
		query.executeUpdate();
		currentSession().delete(group);
	}

	/**
	 * Updates a group from the database.
	 *
	 * @param group The group to be updated.
	 */
	public void update(Group group) {
		currentSession().update(group);
	}

	/**
	 * Finds all groups from the database. Equivalent to "SELECT * FROM groups".
	 * <p>
	 * WARNING: At large scales, this can crash the server. There might be MANY groups in the database.
	 *
	 * @return A List of all the groups.
	 */
	@SuppressWarnings("unchecked")
	public List<Group> findAll() {
		return list((Query<Group>) namedQuery("ro.lbi.sqliggybank.server.Core.Group.findAll"));
	}
}
