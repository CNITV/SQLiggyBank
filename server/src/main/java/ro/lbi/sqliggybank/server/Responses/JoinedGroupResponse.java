package ro.lbi.sqliggybank.server.Responses;

import javax.ws.rs.core.Response;
import java.util.Objects;
import java.util.UUID;

public class JoinedGroupResponse extends GenericResponse {
	private UUID uuid;

	public JoinedGroupResponse(UUID uuid) {
		super(Response.Status.OK.getStatusCode(), "Joined group!");
		this.uuid = uuid;
	}

	public JoinedGroupResponse(UUID uuid, int statusCode, String message) {
		super(statusCode, message);
		this.uuid = uuid;
	}

	public UUID getUuid() {
		return uuid;
	}

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
