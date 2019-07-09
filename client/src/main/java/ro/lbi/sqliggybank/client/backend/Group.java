package ro.lbi.sqliggybank.client.backend;

import lombok.Getter;

import java.util.UUID;

/**
 * This class represents a group. A new group is automatically created by GSON when the group gets retrieved from the database.
 *
 * <p>
 * WARNING: A Group instance is not supposed to be created directly by the programmer.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2019-05-13
 */
@Getter
public class Group {

	/**
	 * The UUID of the group.
	 */
	private UUID uuid;

	/**
	 * The name of the group.
	 */
	private String name;

	/**
	 * The description of the group.
	 */
	private String description;

	/**
	 * The owner of the group (represented by a User class).
	 *
	 * @see User
	 */
	private User owner;

	@Override
	public String toString() {
		return "Name: " + name + '\n' +
				"Description: " + (description == null ? " - " : description) + '\n' +
				"Owner: " + owner.getUsername() + '\n';
	}

}
