package ro.lbi.sqliggybank.server.Core;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.*;

public class Invite {
	private UUID uuid;
	private String group;
	private Date dateCreated;

	public Invite(String group, Date dateCreated) {
		this.uuid = UUID.randomUUID();
		this.group = group;
		this.dateCreated = dateCreated;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getGroup() {
		return group;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Invite)) return false;
		Invite invite = (Invite) o;
		return uuid.equals(invite.uuid) &&
				group.equals(invite.group) &&
				dateCreated.equals(invite.dateCreated);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, group, dateCreated);
	}
}
