package ro.lbi.sqliggybank.server.Responses;

import javax.ws.rs.core.Response;
import java.util.Objects;

/**
 * InternalErrorResponse is a specialized version of GenericResponse that returns a 500 Internal Server Error response.
 *
 * @author StormFireFox1
 * @since 2018-11-25
 */
public class InternalErrorResponse extends GenericResponse {
	private String error;

	public InternalErrorResponse(String error) {
		super(Response.Status.INTERNAL_SERVER_ERROR.toString(), "Internal server error! Contact system administrator for details");
		this.error = error;
	}

	public InternalErrorResponse(String statusCode, String message, String error) {
		super(statusCode, message);
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof InternalErrorResponse)) return false;
		if (!super.equals(o)) return false;
		InternalErrorResponse that = (InternalErrorResponse) o;
		return Objects.equals(error, that.error);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), error);
	}
}
