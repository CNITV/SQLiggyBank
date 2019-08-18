package ro.lbi.sqliggybank.server.Responses;

import javax.ws.rs.core.Response;
import java.util.Objects;
import java.util.UUID;

/**
 * JoinedGroupResponse is a specialized version of GenericResponse that stores the UUID of a recently joined group.
 *
 * @author StormFireFox1
 * @see ro.lbi.sqliggybank.server.Responses.GenericResponse
 * @since 2019-05-24
 */
public class JoinedGroupResponse extends GenericResponse {
	/**
	 * The UUID of the recently joined group.
	 */
	private UUID uuid;

	/**
	 * The main constructor for JoinedGroupResponse.
	 *
	 * @param uuid The UUID that we want the JoinedGroupResponse associated with.
	 */
	public JoinedGroupResponse(UUID uuid) {
		super(Response.Status.OK.getStatusCode(), "Joined group!");
		this.uuid = uuid;
	}

	/**
	 * The constructor for JoinedGroupResponse that also modifies details of the superclass GenericResponse.
	 *
	 * @param uuid The UUID that we want the JoinedGroupResponse associated with.
	 * @param statusCode The status code of the response.
	 * @param message The message of the response.
	 */
	public JoinedGroupResponse(UUID uuid, int statusCode, String message) {
		super(statusCode, message);
		this.uuid = uuid;
	}

	/**
	 * Gets the UUID of the group attached to this response.
	 *
	 * @return The UUID of the group attached to this response.
	 */
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * Sets the UUID of the group attached to this response.
	 *
	 * @param uuid The new UUID of the group attached to this response.
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof JoinedGroupResponse)) return false;
		if (!super.equals(o)) return false;
		JoinedGroupResponse that = (JoinedGroupResponse) o;
		return uuid.equals(that.uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), uuid);
	}
}
