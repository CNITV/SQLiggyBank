package ro.lbi.sqliggybank.client.backend;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * This class represents a goal. A new goal is automatically created by GSON when the group gets retrieved from the database.
 * <p>
 * WARNING: A Goal instance is not supposed to be created directly by the programmer.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2019-07-09
 */
public class Goal {

	/**
	 * The UUID of the goal.
	 */
	private UUID uuid;

	/**
	 * The name of the goal.
	 */
	private String name;

	/**
	 * The description of the goal.
	 */
	private String description;

	/**
	 * The target amount of money for the goal.
	 */
	private int target_amount;

	/**
	 * The deadline for the goal.
	 */
	private Date deadline;

	/**
	 * The bank this goal belongs to.
	 */
	private Bank bank;

	public String getDescription() {
		return description != null ? description : "";
	}

	@Override
	public String toString() {
		SimpleDateFormat format = new SimpleDateFormat("E dd.MM.yyyy',' kk:mm:ss");

		return "Name: " + name + '\n' +
				"Description: " + (description == null ? " - " : description) + '\n' +
				"Target amount: " + target_amount + '\n' +
				"Deadline: " + format.format(deadline) + '\n' +
				"Bank: " + bank.getName() + '\n';
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public String getName() {
		return this.name;
	}

	public int getTarget_amount() {
		return this.target_amount;
	}

	public Date getDeadline() {
		return this.deadline;
	}

	public Bank getBank() {
		return this.bank;
	}
}
