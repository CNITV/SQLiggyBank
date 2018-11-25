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
	private String statusCode;
	private String message;

	GenericResponse() {
	}

	public GenericResponse(String statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

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
