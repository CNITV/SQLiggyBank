package ro.lbi.sqliggybank.server.Core;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.*;

@Entity
@Table(name = "goals")
@NamedQueries(
		{
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.Goal.findAll",
						query = "SELECT g FROM Goal g"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.Goal.findByID",
						query = "SELECT g FROM Goal g WHERE g.uuid = :uuid"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.Goal.findByBank",
						query = "SELECT g FROM Goal g WHERE g.bank = :bank"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.Goal.findByNameAndBank",
						query = "SELECT g FROM Goal g WHERE g.name = :name AND g.bank = :bank"
				)
		}
)
public class Goal {

	@Id
	private UUID uuid;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "target_amount", nullable = false)
	private Integer target_amount;

	@Column(name = "deadline", nullable = false)
	private Date deadline;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "piggy_bank_uuid", nullable = false)
	private PiggyBank bank;

	public Goal() {
	}

	public Goal(UUID uuid, String name, String description, Integer target_amount, Date deadline, PiggyBank bank) {
		this.uuid = uuid;
		this.name = name;
		this.description = description;
		this.target_amount = target_amount;
		this.deadline = deadline;
		this.bank = bank;
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

	public Integer getTarget_amount() {
		return target_amount;
	}

	public Date getDeadline() {
		return deadline;
	}

	public PiggyBank getBank() {
		return bank;
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

	public void setTarget_amount(Integer target_amount) {
		this.target_amount = target_amount;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public void setBank(PiggyBank bank) {
		this.bank = bank;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Goal)) return false;
		Goal goal = (Goal) o;
		return Objects.equals(uuid, goal.uuid) &&
				Objects.equals(name, goal.name) &&
				Objects.equals(description, goal.description) &&
				Objects.equals(target_amount, goal.target_amount) &&
				Objects.equals(deadline, goal.deadline) &&
				Objects.equals(bank, goal.bank);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, name, description, target_amount, deadline, bank);
	}
}
