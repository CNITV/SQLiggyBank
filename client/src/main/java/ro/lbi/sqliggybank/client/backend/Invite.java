package ro.lbi.sqliggybank.client.backend;

import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;
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
	 * The name of the group the invite belongs to.
	 */
	private String groupName;

	/**
	 * The creation date of the invite.
	 */
	private Date dateCreated;

	@Override
	public String toString() {
		SimpleDateFormat format = new SimpleDateFormat("E dd.MM.yyyy',' kk:mm:ss");

		return uuid + " (" + format.format(dateCreated) + ")\n";
	}
}
