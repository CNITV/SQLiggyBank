package ro.lbi.sqliggybank.server.Core;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.*;

/**
 * Group is a class that represents a group of SQLiggyBank. 
 *
 * It contains a name, description and an owner. Groups are made of multiple
 * members, represented by users, whose list can be found in the database under
 * the GroupEntry table, "group_lists".
 *
 * @author StormFireFox1
 * @since 2018-11-24
 */
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

	/**
	 * The UUID of the group.
	 */
	@Id
	private UUID uuid;

	/**
	 * The name of the group.
	 */
	@Column(name = "name", nullable = false)
	private String name;
	/**
	 * The description of the group.
	 */
	@Column(name = "description")
	private String description;

	/**
	 * The owner of the group.
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_uid", nullable = false)
	private User owner;

	/**
	 * Default constructor, used by Hibernate to enable useful casting for persistence, among other things.
	 */
	public Group() {
	}

	/**
	 * The main constructor for Group.
	 *
	 * @param uuid 	      The UUID of the group.
	 * @param name        The name of the group.
	 * @param description The description of the group.
	 * @param owner	      The owner of the group.
	 */
	public Group(UUID uuid, String name, String description, User owner) {
		if (name == null || name.equals("")) {
			throw new IllegalArgumentException("Name must exist and cannot be empty!");
		}
		this.uuid = uuid;
		this.name = name;
		this.description = description;
		this.owner = owner;
	}

	/**
	 * Gets the UUID of the group.
	 *
	 * @return The UUID of the group.
	 */
	@JsonIgnore
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * Gets the name of the group.
	 *
	 * @return The name of the group.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the description of the group.
	 *
	 * @return The description of the group.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * gets the owner of the group.
	 *
	 * @return the owner of the group.
	 */
	public User getOwner() {
		return owner;
	}

	/**
	 * Sets the UUID of the group.
	 *
	 * @param uuid The new UUID to change to.
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Sets the name of the group.
	 *
	 * @param name The new name to change to.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the description of the group.
	 *
	 * @param description The new description to change to.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the owner of the group.
	 *
	 * @param owner The new owner to change to.
	 */
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
