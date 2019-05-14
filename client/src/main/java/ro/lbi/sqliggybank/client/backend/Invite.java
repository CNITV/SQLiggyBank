package ro.lbi.sqliggybank.client.backend;

import lombok.Getter;

import java.util.UUID;

/**
 * This class represents an invite. A new invite is automatically created by GSON when the database creates an invite link.
 *
 * <p>
 * WARNING: An Invite instance is not supposed to be created directly by the programmer.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2019-05-13
 */
@Getter
public class Invite {

	/**
	 * The UUID of the invite.
	 */
	private UUID uuid;

	/**
	 * The name of the group.
	 */
	private String groupName;

	/**
	 * The description of the group.
	 */
	private long dateCreated;

	@Override
	public String toString() {
		return "UUID: " + uuid + "\n" +
				"Group name: " + groupName + "\n" +
				"Date created: " + dateCreated + "\n";
	}
}
