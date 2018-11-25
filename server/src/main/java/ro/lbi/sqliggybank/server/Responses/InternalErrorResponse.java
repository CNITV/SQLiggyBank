package ro.lbi.sqliggybank.server.Responses;

import javax.ws.rs.core.Response;
import java.util.Objects;

/**
 * InternalErrorResponse is a specialized version of GenericResponse that returns a 500 Internal Server Error response.
 *
 * @author StormFireFox1
 * @see ro.lbi.sqliggybank.server.Responses.GenericResponse
 * @since 2018-11-25
 */
public class InternalErrorResponse extends GenericResponse {
	/**
	 * The details for the internal server error
	 */
	private String error;

	/**
	 * The main constructor for the InternalErrorResponse.
	 *
	 * @param error The error of the response.
	 */
	public InternalErrorResponse(String error) {
		super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Internal server error! Contact system administrator for details");
		this.error = error;
	}

	/**
	 * The constructor for InternalErrorResponse that also modifies details of the superclass GenericResponse.
	 *
	 * @param statusCode The status code of the response.
	 * @param message    The message of the response.
	 * @param error      The error of the response.
	 */
	public InternalErrorResponse(int statusCode, String message, String error) {
		super(statusCode, message);
		this.error = error;
	}

	/**
	 * Gets the error of the response.
	 *
	 * @return The error of the response.
	 */
	public String getError() {
		return error;
	}

	/**
	 * Sets the error of the response.
	 *
	 * @param error The new error for the response.
	 */
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
