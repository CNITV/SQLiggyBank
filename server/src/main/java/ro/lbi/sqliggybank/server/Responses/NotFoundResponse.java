package ro.lbi.sqliggybank.server.Responses;

import javax.ws.rs.core.Response;
import java.util.Objects;

public class NotFoundResponse extends GenericResponse {
	private String details;

	public NotFoundResponse(String details) {
		super(Response.Status.NOT_FOUND.toString(), "Resource not found!");
		this.details = details;
	}

	public NotFoundResponse(String statusCode, String message, String details) {
		super(statusCode, message);
		this.details = details;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof NotFoundResponse)) return false;
		if (!super.equals(o)) return false;
		NotFoundResponse that = (NotFoundResponse) o;
		return Objects.equals(details, that.details);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), details);
	}
}
