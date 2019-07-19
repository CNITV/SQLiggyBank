package ro.lbi.sqliggybank.server.Core;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.*;

/**
 * Deposit is a class that represents a deposit of SQLiggyBank.
 *
 * It contains the amount transferred to the bank, the piggy bank it is in, the
 * payee, and the timestamp of the deposit, as well as some tags.
 * <p>
 * Deposits are immutable, hence the lack of external methods to modify the
 * transaction. 
 *
 * @author StormFireFox1
 * @since 2019-03-28
 */
@Entity
@Table(name = "deposits")
@NamedQueries(
		{
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.Deposit.findAll",
						query = "SELECT d FROM Deposit d"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.Deposit.findByID",
						query = "SELECT d FROM Deposit d WHERE d.uuid = :uuid"
				),
				@NamedQuery(
						name = "ro.lbi.sqliggybank.server.Core.Deposit.findByGroupAndBank",
						query = "SELECT d FROM Deposit d WHERE d.bank.name = :bank AND d.bank.group.name = :group"
				)
		}
)
public class Deposit {

	/**
	 * The UUID of the transaction.
	 */
	@Id
	private UUID uuid;

	/**
	 * The amount of the deposit.
	 */
	@Column(name = "amount", nullable = false)
	private Integer amount;

	/**
	 * The piggy bank that the deposit is linked to.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "piggy_bank_uuid", nullable = false)
	private PiggyBank bank;

	/**
	 * The payee of the deposit.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payee_uuid", nullable = false)
	private User payee;

	/**
	 * The timestamp of the deposit.
	 */
	@Column(name = "timestamp", nullable = false)
	private Date timestamp;

	/**
	 * Tags, optional words associated to the deposit.
	 */
	@Column(name = "tags")
	private String tags;

	/**
	 * Default constructor, used by Hibernate to enable useful casting for persistence, among other things.
	 */
	public Deposit() {
	}

	/**
	 * The main constructor for Deposit.
	 *
	 * @param uuid 	    The UUID of the deposit.
	 * @param payee     The payee of the deposit.
	 * @param timestamp The timestamp of the deposit.
	 * @param tags      Tags, optional for the deposit.
	 */
	public Deposit(UUID uuid, Integer amount, PiggyBank bank, User payee, Date timestamp, String tags) {
		this.uuid = uuid;
		this.amount = amount;
		this.bank = bank;
		this.payee = payee;
		this.timestamp = timestamp;
		this.tags = tags;
	}

	/**
	 * Gets the UUID of the deposit.
	 *
	 * @return The UUID of the deposit.
	 */
	@JsonIgnore
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * Gets the amount of the deposit.
	 *
	 * @return The amount of the deposit.
	 */
	public Integer getAmount() {
		return amount;
	}

	/**
	 * Gets the piggy bank of the deposit.
	 *
	 * @return The piggy bank of the deposit.
	 */
	public PiggyBank getBank() {
		return bank;
	}

	/**
	 * Gets the payee of the deposit.
	 *
	 * @return The payee of the deposit.
	 */
	public User getPayee() {
		return payee;
	}

	/**
	 * Gets the timestamp of the deposit.
	 *
	 * @return The timestamp of the deposit.
	 */
	public Date getTimestamp() {
		return timestamp;
	}

 	/**
	 * Gets the tags of the deposit.
	 *
	 * @return The tags of the deposit.
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * Sets the UUID of the deposit.
	 *
	 * @param uuid The UUID to change to.
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Sets the amount of the deposit.
	 *
	 * @param amount The amount to change to.
	 */
	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	/**
	 * Sets the piggy bank of the deposit.
	 *
	 * @param uuid The piggy bank to change to.
	 */
	public void setBank(PiggyBank bank) {
		this.bank = bank;
	}

	/**
	 * Sets the payeeof the deposit.
	 *
	 * @param payee The payee to change to.
	 */
	public void setPayee(User payee) {
		this.payee = payee;
	}

	/**
	 * Sets the timestamp of the deposit.
	 *
	 * @param timestamp The timestamp to change to.
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Sets the tags of the deposit.
	 *
	 * @param uuid The tags to change to.
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Deposit)) return false;
		Deposit deposit = (Deposit) o;
		return Objects.equals(uuid, deposit.uuid) &&
				Objects.equals(amount, deposit.amount) &&
				Objects.equals(bank, deposit.bank) &&
				Objects.equals(payee, deposit.payee) &&
				Objects.equals(timestamp, deposit.timestamp) &&
				Objects.equals(tags, deposit.tags);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, amount, bank, payee, timestamp, tags);
	}
}
