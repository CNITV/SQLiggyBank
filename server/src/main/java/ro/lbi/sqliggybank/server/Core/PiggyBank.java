package ro.lbi.sqliggybank.server.Core;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.*;
/**
 * PiggyBank is the class representing a piggy bank of SQLiggyBank.
 *
 * It contains a name, description, and a group to which it belongs to.
 *
 * @author StormFireFox1
 * @since 2019-03-28
 */
@Entity
@Table(name = "banks")
@NamedQueries(
		{
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.PiggyBank.findAll",
						query = "SELECT b FROM PiggyBank b"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.PiggyBank.findByID",
						query = "SELECT b FROM PiggyBank b WHERE b.uuid = :uuid"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.PiggyBank.findByName",
						query = "SELECT b FROM PiggyBank b WHERE b.name = :name"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.PiggyBank.findByGroup",
						query = "SELECT b FROM PiggyBank b WHERE b.group = :passed_group"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.PiggyBank.findByGroupAndName",
						query = "SELECT b FROM PiggyBank b WHERE b.group = :passed_group AND b.name = :name"
				),
		}
)
public class PiggyBank {

	/**
	 * The UUID of the piggy bank.
	 */
	@Id
	private UUID uuid;

	/**
	 * The name of the piggy bank.
	 */
	@Column(name = "name", nullable = false)
	private String name;

	/**
	 * The description of the piggy bank.
	 */
	@Column(name = "description")
	private String description;

	/**
	 * The group of the piggy bank.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_uuid")
	private Group group;

	/**
	 * Default constructor, used by Hibernate to enable useful casting for persistence, among other things.
	 */
	public PiggyBank() {
	}

	/**
	 * The main constructor for PiggyBank.
	 *
	 * @param uuid        The UUID of the piggy bank.
	 * @param name        The name of the piggy bank.
	 * @param description The description of the piggy bank.
	 * @param group       The group of the piggy bank. 
	 */
	public PiggyBank(UUID uuid, String name, String description, Group group) {
		this.uuid = uuid;
		this.name = name;
		this.description = description;
		this.group = group;
	}

	/**
	 * Gets the UUID of the piggy bank.
	 *
	 * @return The UUID of the piggy bank.
	 */
	@JsonIgnore
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * Gets the name of the piggy bank.
	 *
	 * @return The name of the piggy bank.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the description of the piggy bank.
	 *
	 * @return The description of the piggy bank.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the group that the piggy bank is linked to.
	 *
	 * @return The group that the piggy bank is linked to.
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * Sets the UUID of the piggy bank.
	 * 
	 * @param uuid The new UUID to change to.
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Sets the name of the piggy bank.
	 *
	 * @param name The new name to change to.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the description of the piggy bank.
	 *
	 * @param description The new description to change to.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the new group to link the piggy bank to.
	 *
	 * @param group The new group to link to.
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PiggyBank)) return false;
		PiggyBank piggyBank = (PiggyBank) o;
		return uuid.equals(piggyBank.uuid) &&
				name.equals(piggyBank.name) &&
				description.equals(piggyBank.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, name, description);
	}
}
