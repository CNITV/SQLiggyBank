package ro.lbi.sqliggybank.server.Core;


import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.*;

/**
 * Withdrawal is a class that represents a withdrawal of SQLiggyBank.
 *
 * It contains the amount extracted from the bank, the piggy bank it is in, the
 * timestamp of the withdrawal, as well as some tags.
 *
 * Withdrawals are immutable, hence the lack of external methods to modify the
 * withdrawal. In addition, withdrawals do not have a specific payee, as
 * technically, only the group owner should be allowed to "break" the piggy
 * bank.
 *
 * @author StormFireFox1
 * @since 2019-03-28
 */
@Entity
@Table(name = "withdrawals")
@NamedQueries(
		{
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.Withdrawal.findAll",
						query = "SELECT w FROM Withdrawal w"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.Withdrawal.findByID",
						query = "SELECT w FROM Withdrawal w WHERE w.uuid = :uuid"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.Withdrawal.findByGroupAndBank",
						query = "SELECT w FROM Withdrawal w WHERE w.bank.name = :bank AND w.bank.group.name = :group"
				)
		}
)
public class Withdrawal {

	/**
	 * The UUID of the transaction.
	 */
	@Id
	private UUID uuid;

	/**
	 * The amount of the withdrawal.
	 */
	@Column(name = "amount", nullable = false)
	private Integer amount;

	/**
	 * The bank from which the withdrawal was made.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "piggy_bank_uuid", nullable = false)
	private PiggyBank bank;

	/**
	 * The date of creation of the withdrawal.
	 */
	@Column(name = "timestamp", nullable = false)
	private Date timestamp;

	/**
	 * Tags, optional words associated to the withdrawal.
	 */
	@Column(name = "tags")
	private String tags;

	/**
	 * Default constructor, used by Hibernate to enable useful casting for persistence, among other things.
	 */
	public Withdrawal() {
	}

	/**
	 * The main constructor for Withdrawal.
	 *
	 * @param uuid 	    The UUID of the withdrawal.
	 * @param amount    The amount of the withdrawal.
	 * @param timestamp The timestamp of the withdrawal.
	 * @param tags      Tags, optional for the withdrawal.
	 */
	public Withdrawal(UUID uuid, Integer amount, PiggyBank bank, Date timestamp, String tags) {
		this.uuid = uuid;
		this.amount = amount;
		this.bank = bank;
		this.timestamp = timestamp;
		this.tags = tags;
	}

	/**
	 * Gets the UUID of the withdrawal.
	 *
	 * @return The UUID of the withdrawal.
	 */
	@JsonIgnore
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * Gets the amount of the withdrawal.
	 *
	 * @return The amount of the withdrawal.
	 */
	public Integer getAmount() {
		return amount;
	}

	/**
	 * Gets the piggy bank of the withdrawal.
	 *
	 * @return The piggy bank of the withdrawal.
	 */
	public PiggyBank getBank() {
		return bank;
	}

	/**
	 * Gets the timestamp of the withdrawal.
	 *
	 * @return The timestamp of the withdrawal.
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * Gets the tags of the withdrawal.
	 *
	 * @return The tags of the withdrawal.
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * Sets the UUID of the withdrawal.
	 *
	 * @param uuid The UUID to change to.
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Sets the amount of the withdrawal.
	 *
	 * @param amount The amount to change to.
	 */
	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	/**
	 * Sets the piggy bank of the withdrawal.
	 *
	 * @param uuid The piggy bank to change to.
	 */
	public void setBank(PiggyBank bank) {
		this.bank = bank;
	}

	/**
	 * Sets the timestamp of the withdrawal.
	 *
	 * @param timestamp The timestamp to change to.
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Sets the tags of the withdrawal.
	 *
	 * @param uuid The tags to change to.
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Withdrawal)) return false;
		Withdrawal that = (Withdrawal) o;
		return Objects.equals(uuid, that.uuid) &&
				Objects.equals(amount, that.amount) &&
				Objects.equals(bank, that.bank) &&
				Objects.equals(timestamp, that.timestamp) &&
				Objects.equals(tags, that.tags);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, amount, bank, timestamp, tags);
	}
}
