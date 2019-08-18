package ro.lbi.sqliggybank.server.Core;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.*;

/**
 * Goal is a class that represents a goal of SQLiggyBank.
 *
 * It contains a name, description, target amount, deadline and is linked to a
 * specific piggy bank.
 *
 * @author StormFireFox1
 * @since 2018-11-24
 */
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
	/**
	 * The UUID of the goal.
	 */
	@Id
	private UUID uuid;

	/**
	 * The name of the goal.
	 */
	@Column(name = "name", nullable = false)
	private String name;

	/**
	 * The description of the goal.
	 */
	@Column(name = "description")
	private String description;

	/**
	 * The target amount of the goal.
	 */
	@Column(name = "target_amount", nullable = false)
	private Integer target_amount;

	/**
	 * The deadline of the goal.
	 */
	@Column(name = "deadline", nullable = false)
	private Date deadline;

	/**
	 * The piggy bank the goal is linked to. 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "piggy_bank_uuid", nullable = false)
	private PiggyBank bank;

	/**
	 * Default constructor, used by Hibernate to enable useful casting for persistence, among other things.
	 */
	public Goal() {
	}

	/**
	 * The main constructor for Goal.
	 *
	 * @param uuid 	        The UUID of the goal.
	 * @param description   The description of the goal.
	 * @param target_amount The target amount of the goal.
	 * @param deadline 	The deadline of the goal.
	 * @param bank		The piggy bank the goal is linked to.
	 */
	public Goal(UUID uuid, String name, String description, Integer target_amount, Date deadline, PiggyBank bank) {
		this.uuid = uuid;
		this.name = name;
		this.description = description;
		this.target_amount = target_amount;
		this.deadline = deadline;
		this.bank = bank;
	}

	/**
	 * Gets the UUID of the goal.
	 *
	 * @return The UUID of the goal.
	 */
	@JsonIgnore
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * Gets the UUID of the goal.
	 *
	 * @return The UUID of the goal.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the description of the goal.
	 *
	 * @return The description of the goal.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Gets the target amount of the goal.
	 *
	 * @return The target amount of the goal.
	 */
	public Integer getTarget_amount() {
		return target_amount;
	}

	/**
	 * Gets the deadline of the goal.
	 *
	 * @return The description of the goal.
	 */
	public Date getDeadline() {
		return deadline;
	}
	
	/**
	 * Gets the bank of the goal.
	 *
	 * @return The bank of the goal.
	 */
	public PiggyBank getBank() {
		return bank;
	}

	/**
	 * Sets the UUID of the goal.
	 *
	 * @param uuid The UUID of the goal.
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Sets the name of the goal.
	 *
	 * @param name The name of the goal.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the description of the goal.
	 *
	 * @param description The description of the goal.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the target amount of the goal.
	 *
	 * @param target_amount The target amount of the goal.
	 */
	public void setTarget_amount(Integer target_amount) {
		this.target_amount = target_amount;
	}

	/**
	 * Sets the deadline of the goal.
	 *
	 * @param deadline The deadline of the goal.
	 */
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	/**
	 * Sets the piggy bank of the goal.
	 *
	 * @param description The piggy bank of the goal.
	 */
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
