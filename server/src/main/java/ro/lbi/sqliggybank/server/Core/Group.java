package ro.lbi.sqliggybank.server.Core;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.*;

@Entity
@Table(name = "groups")
@NamedQueries(
		{
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.Group.findAll",
						query = "SELECT g FROM Group g"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.Group.findByID",
						query = "SELECT g FROM Group g WHERE g.uuid = :uuid"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.Group.findByName",
						query = "SELECT g FROM Group g WHERE g.name = :name"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.Group.findByOwner",
						query = "SELECT g FROM Group g WHERE g.owner = :owner_uuid"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.Group.isUserOwnerOfGroup",
						query = "SELECT g FROM Group g WHERE g.owner.username = :username AND g.name = :groupName"
				),
		})
public class Group {

	@Id
	private UUID uuid;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description")
	private String description;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_uid", nullable = false)
	private User owner;

	public Group() {
	}

	public Group(UUID uuid, String name, String description, User owner) {
		if (name == null || name.equals("")) {
			throw new IllegalArgumentException("Name must exist and cannot be empty!");
		}
		this.uuid = uuid;
		this.name = name;
		this.description = description;
		this.owner = owner;
	}

	@JsonIgnore
	public UUID getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public User getOwner() {
		return owner;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Group)) return false;
		Group group = (Group) o;
		return uuid.equals(group.uuid) &&
				name.equals(group.name) &&
				description.equals(group.description) &&
				owner.equals(group.owner);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, name, description, owner);
	}
}
