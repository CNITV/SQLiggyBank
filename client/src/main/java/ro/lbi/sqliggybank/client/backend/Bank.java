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
 * @since 2019-05-14
 */
@Getter
public class Bank {

	/**
	 * The UUID of the bank.
	 */
	private UUID uuid;

	/**
	 * The name of the bank.
	 */
	private String name;

	/**
	 * The description of the bank.
	 */
	private String description;

	/**
	 * The group who the bank belongs to.
	 */
	private Group group;

	@Override
	public String toString() {
		return "UUID: " + uuid + "\n" +
				"Name: " + name + "\n" +
				"Description: " + description + "\n" +
				"Group: " + group + "\n";
	}

}
