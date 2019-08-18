package ro.lbi.sqliggybank.server.Core;

import javax.persistence.*;
import java.util.Objects;

/**
 * GroupEntry is a class that represents a group list of SQLiggyBank. 
 *
 * It contains a user and a group and represents a membership of a user to a
 * specific group. 
 *
 * @author StormFireFox1
 * @since 2018-11-24
 */
@Entity
@Table(name = "group_lists")
@NamedQueries(
		{
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.GroupEntry.findAll",
						query = "SELECT l FROM GroupEntry l"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.GroupEntry.findByID",
						query = "SELECT l FROM GroupEntry l WHERE l.id = :id"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.GroupEntry.findByGroup",
						query = "SELECT l FROM GroupEntry l WHERE l.group = :passed_group"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.GroupEntry.isUserPartOfGroup",
						query = "SELECT l FROM GroupEntry l WHERE l.user.username = :username AND l.group.name = :groupName"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.GroupEntry.deleteUsersFromGroup",
						query = "DELETE FROM GroupEntry l WHERE l.group IN (SELECT grp FROM Group grp WHERE grp.name = :groupName)"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.GroupEntry.membersOfGroup",
						query = "SELECT DISTINCT l.user FROM GroupEntry l, Group g WHERE l.group.uuid = :groupUuid"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.GroupEntry.groupsOfUser",
						query = "SELECT DISTINCT l.group FROM GroupEntry l, User u WHERE l.user.uuid = :userUuid"
				)
		})
public class GroupEntry {
	/**
	 * The ID of the group entry.
	 *
	 * Not inherently needed to be special, since group entries are only
	 * used internally.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_lists_id")
	@SequenceGenerator(name = "group_lists_id", sequenceName = "group_lists_id_seq", allocationSize = 1)
	private Integer id;

	/**
	 * The user associated with the group entry.
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_uuid")
	private User user;

	/**
	 * The group associated with the group entry.
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_uuid")
	private Group group;

	/**
	 * Default constructor, used by Hibernate to enable useful casting for persistence, among other things.
	 */
	public GroupEntry () {

	}

	/**
	 * The main constructor for GroupEntry.
	 *
	 * @param user  The user of the group entry.
	 * @param group The group of the group entry.
	 */
	public GroupEntry(User user, Group group) {
		this.user = user;
		this.group = group;
	}

	/**
	 * Gets the ID of the group entry.
	 *
	 * @return The ID of the group entry.
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Gets the user of the group entry.
	 *
	 * @return The user of the group entry.
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Gets the group of the group entry.
	 *
	 * @return The group of the group entry.
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * Sets the ID of the group entry.
	 *
	 * @param id The new ID to change to.
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Sets the user of the group entry.
	 *
	 * @param user The new user to change to.
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Sets the group of the group entry.
	 *
	 * @param group The new group to change to.
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GroupEntry)) return false;
		GroupEntry that = (GroupEntry) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(user, that.user) &&
				Objects.equals(group, that.group);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, user, group);
	}
}
