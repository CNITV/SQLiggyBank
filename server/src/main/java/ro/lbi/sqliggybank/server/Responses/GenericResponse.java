package ro.lbi.sqliggybank.server.Responses;

import java.util.Objects;

/**
 * GenericResponse is an object that holds a response from the SQLiggyBank server. This was made in order to automatically
 * serialize server responses to JSON using Jackson.
 *
 * @author StormFireFox1
 * @since 2018-11-25
 */
public class GenericResponse {
	/**
	 * The status code of the response.
	 */
	private int statusCode;
	/**
	 * The message of the response.
	 */
	private String message;

	/**
	 * The default constructor of the response.
	 * Needed for the super() methods.
	 */
	GenericResponse() {
	}

	/**
	 * The main constructor for GenericResponse.
	 *
	 * @param statusCode The status code of the response.
	 * @param message The message of the response.
	 */
	public GenericResponse(int statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}

	/**
	 * Gets the status code of the response.
	 *
	 * @return The status code of the response.
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Sets the status code of the response.
	 *
	 * @param statusCode The new status code for the response.
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Gets the message of the response.
	 *
	 * @return The message of the response.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message of the response.
	 *
	 * @param message The new message for the response.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GenericResponse)) return false;
		GenericResponse that = (GenericResponse) o;
		return Objects.equals(statusCode, that.statusCode) &&
				Objects.equals(message, that.message);
	}

	@Override
	public int hashCode() {
		return Objects.hash(statusCode, message);
	}
}
