package ro.lbi.sqliggybank.server.Core;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.*;

/**
 * Invite is a class representing an invite of SQLiggyBank.
 *
 * It contains an UUID, group and timestamp of creation. This is used to allow
 * members to join groups, since such groups are private.
 *
 * @author StormFireFox1
 * @author 2019-03-28
 */
public class Invite {
	/**
	 * The UUID of the invite.
	 */
	private UUID uuid;
	/**
	 * The group that the invite belongs to.
	 */
	private String group;
	/**
	 * The date of creation of the invite.
	 */
	private Date dateCreated;

	/**
	 * The main constructor for Invite.
	 *
	 * @param group       The group that the invite belongs to.
	 * @param dateCreated The date of creation of the invite.
	 */
	public Invite(String group, Date dateCreated) {
		this.uuid = UUID.randomUUID();
		this.group = group;
		this.dateCreated = dateCreated;
	}
	
	/**
	 * Gets the UUID of the invite.
	 *
	 * @return The UUID of the invite.
	 */
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * Gets the group of the invite.
	 *
	 * @return The group of the invite.
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * Gets the date of creation for the invite.
	 *
	 * @return The date of creation of the invite.
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * Sets the UUID of the invite.
	 *
	 * @param uuid The new UUID to change to.
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Sets the group of the invite.
	 *
	 * @param group The new group to change to.
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * Sets the date of creation of the invite.
	 *
	 * @param group The new date of creation to change to.
	 */
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
