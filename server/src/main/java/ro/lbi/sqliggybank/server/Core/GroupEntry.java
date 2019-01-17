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
						query = "SELECT l FROM GroupEntry l WHERE l.id = :id"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.GroupEntry.isUserPartOfGroup",
						query = "SELECT l FROM GroupEntry l WHERE l.user.username = :username AND l.group.name = :groupName"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.GroupEntry.deleteUsersFromGroup",
						query = "DELETE FROM GroupEntry l WHERE l.group IN (SELECT grp FROM Group grp WHERE grp.name = :groupName)"
				)
		})
public class GroupEntry {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_lists_id")
	@SequenceGenerator(name = "group_lists_id", sequenceName = "group_lists_id_seq", allocationSize = 1)
	private Integer id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_uuid")
	private User user;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_uuid")
	private Group group;

	public GroupEntry () {

	}

	public GroupEntry(User user, Group group) {
		this.user = user;
		this.group = group;
	}

	public Integer getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public Group getGroup() {
		return group;
	}

	public void setId(Integer id) {
		this.id = id;
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
		return Objects.equals(id, that.id) &&
				Objects.equals(user, that.user) &&
				Objects.equals(group, that.group);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, user, group);
	}
}
