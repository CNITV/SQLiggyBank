package ro.lbi.sqliggybank.server.Core;

import org.eclipse.jetty.util.annotation.Name;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

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

	@Id
	private UUID uuid;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_uuid")
	private Group group;


	public PiggyBank() {
	}

	public PiggyBank(UUID uuid, String name, String description, Group group) {
		this.uuid = uuid;
		this.name = name;
		this.description = description;
		this.group = group;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Group getGroup() {
		return group;
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
