package ro.lbi.sqliggybank.server.Responses;

import javax.ws.rs.core.Response;
import java.util.Objects;

/**
 * NotFoundResponse is a specialized version of GenericResponse that returns a 404 Resource Not Found response.
 *
 * @author StormFireFox1
 * @see ro.lbi.sqliggybank.server.Responses.GenericResponse
 * @since 2018-11-25
 */
public class NotFoundResponse extends GenericResponse {
	/**
	 * The details of the response.
	 * <p>
	 * The resourced that could not be found can be specified here.
	 */
	private String details;

	/**
	 * The main constructor for NotFoundResponse.
	 *
	 * @param details The details of the response.
	 */
	public NotFoundResponse(String details) {
		super(Response.Status.NOT_FOUND.toString(), "Resource not found!");
		this.details = details;
	}

	/**
	 * The constructor for NotFoundResponse that also modifies details of the superclass GenericResponse.
	 *
	 * @param statusCode The status code of the response.
	 * @param message    The message of the response.
	 * @param details    The details of the response.
	 */
	public NotFoundResponse(String statusCode, String message, String details) {
		super(statusCode, message);
		this.details = details;
	}

	/**
	 * Gets the details of the response.
	 *
	 * @return The details of the response.
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * Sets the details of the response.
	 *
	 * @param details The new details for the response.
	 */
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
