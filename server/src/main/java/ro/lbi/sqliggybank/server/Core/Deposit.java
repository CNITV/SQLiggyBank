package ro.lbi.sqliggybank.server.Core;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

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
		}
)
public class Deposit {

	@Id
	private UUID uuid;

	@Column(name = "amount", nullable = false)
	private Integer amount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "piggy_bank_uuid", nullable = false)
	private PiggyBank bank;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payee_uuid", nullable = false)
	private User payee;

	@Column(name = "timestamp", nullable = false)
	private Date timestamp;

	@Column(name = "tags")
	private String tags;

	public Deposit() {
	}

	public Deposit(UUID uuid, Integer amount, PiggyBank bank, User payee, Date timestamp, String tags) {
		this.uuid = uuid;
		this.amount = amount;
		this.bank = bank;
		this.payee = payee;
		this.timestamp = timestamp;
		this.tags = tags;
	}

	public UUID getUuid() {
		return uuid;
	}

	public Integer getAmount() {
		return amount;
	}

	public PiggyBank getBank() {
		return bank;
	}

	public User getPayee() {
		return payee;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getTags() {
		return tags;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public void setBank(PiggyBank bank) {
		this.bank = bank;
	}

	public void setPayee(User payee) {
		this.payee = payee;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

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
