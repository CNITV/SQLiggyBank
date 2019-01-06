package ro.lbi.sqliggybank.server.Core;

import org.eclipse.jetty.util.annotation.Name;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

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
						query = "SELECT l FROM GroupEntry l WHERE l.uuid = :uuid"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.GroupEntry.isUserPartOfGroup",
						query = "SELECT CASE WHEN EXISTS( SELECT group_lists.id, users.username, groups.name FROM group_lists JOIN users ON group_lists.user_uuid = users.uuid JOIN groups ON group_lists.group_uuid = groups.uuid WHERE users.username = :username AND groups.name = :groupName) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END;"
				),
		})
public class GroupEntry {
	@Id
	private UUID uuid;

	@OneToOne
	private User user;

	@OneToOne
	private Group group;

	public GroupEntry(UUID uuid, User user, Group group) {
		this.uuid = uuid;
		this.user = user;
		this.group = group;
	}

	public UUID getUuid() {
		return uuid;
	}

	public User getUser() {
		return user;
	}

	public Group getGroup() {
		return group;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GroupEntry)) return false;
		GroupEntry that = (GroupEntry) o;
		return Objects.equals(uuid, that.uuid) &&
				Objects.equals(user, that.user) &&
				Objects.equals(group, that.group);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, user, group);
	}
}
