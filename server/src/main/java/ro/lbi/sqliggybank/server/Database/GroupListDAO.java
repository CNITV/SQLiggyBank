package ro.lbi.sqliggybank.server.Database;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import ro.lbi.sqliggybank.server.Core.Group;
import ro.lbi.sqliggybank.server.Core.GroupEntry;
import ro.lbi.sqliggybank.server.Core.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GroupListDAO extends AbstractDAO<GroupEntry> {

	private UserDAO userDAO;

	public GroupListDAO(SessionFactory factory, UserDAO userDAO) {
		super(factory);
		this.userDAO = userDAO;
	}

	/**
	 * Finds a group list by ID.
	 *
	 * @param id The UUID of the group list.
	 * @return Optional of any found group list.
	 */
	public Optional<GroupEntry> findByID(UUID id) {
		return Optional.ofNullable(get(id));
	}

	public boolean isUserPartOfGroup(String username, String groupName) {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.GroupEntry.isUserPartOfGroup");
		query.setParameter("username", username);
		query.setParameter("groupName", groupName);
		return !query.getResultList().isEmpty();
	}

	/**
	 * Finds all the users that are members of a group.
	 *
	 * @param group the Group to find the members from.
	 * @return List of all the Users in a group.
	 */
	@SuppressWarnings("unchecked")
	public List<User> membersOfGroup(Group group) {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.GroupEntry.membersOfGroup");
		query.setParameter("groupUuid", group.getUuid());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Group> groupsOfUser(User user) {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.GroupEntry.groupsOfUser");
		query.setParameter("userUuid", user.getUuid());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<GroupEntry> findByGroup(Group group) {
		Query query = namedQuery("ro.lbi.sqliggybank.server.Core.GroupEntry.isUserPartOfGroup");
		query.setParameter("passed_group", group);
		return list((Query<GroupEntry>) query);
	}

	/**
	 * Creates a group list in the database.
	 *
	 * @param groupEntry The group list to be created.
	 */
	public void create(GroupEntry groupEntry) {
		persist(groupEntry);
	}

	public void addUserToGroup(User user, Group group) {
		GroupEntry entry = new GroupEntry(user, group);
		create(entry);
	}

	/**
	 * Deletes a group list from the database.
	 *
	 * @param groupEntry The group list to be deleted.
	 */
	public void delete(GroupEntry groupEntry) {
		currentSession().delete(groupEntry);
	}

	/**
	 * Updates a group list from the database.
	 *
	 * @param groupEntry The group list to be updated.
	 */
	public void update(GroupEntry groupEntry) {
		currentSession().update(groupEntry);
	}

	/**
	 * Finds all group lists from the database. Equivalent to "SELECT * FROM group_lists".
	 * <p>
	 * WARNING: At large scales, this can crash the server. There might be MANY group lists in the database.
	 *
	 * @return A List of all the group lists.
	 */
	@SuppressWarnings("unchecked")
	public List<GroupEntry> findAll() {
		return list((Query<GroupEntry>) namedQuery("ro.lbi.sqliggybank.server.Core.GroupEntry.findAll"));
	}

}
