package ro.lbi.sqliggybank.server.Core;


import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

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
		}
)
public class Withdrawal {

	@Id
	private UUID uuid;

	@Column(name = "amount", nullable = false)
	private Integer amount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "piggy_bank_uuid", nullable = false)
	private PiggyBank bank;

	@Column(name = "timestamp", nullable = false)
	private Date timestamp;

	@Column(name = "tags")
	private String tags;

	public Withdrawal() {
	}

	public Withdrawal(UUID uuid, Integer amount, PiggyBank bank, Date timestamp, String tags) {
		this.uuid = uuid;
		this.amount = amount;
		this.bank = bank;
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

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

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
