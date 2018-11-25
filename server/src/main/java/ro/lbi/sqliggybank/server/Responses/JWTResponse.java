package ro.lbi.sqliggybank.server.Responses;

import javax.ws.rs.core.Response;
import java.util.Objects;

/**
 * JWTResponse is a specialized version of GenericResponse that returns a JWT included with the response.
 *
 * @author StormFireFox1
 * @see ro.lbi.sqliggybank.server.Responses.GenericResponse
 * @see <a href="https://jwt.io">JWT website</a>
 * @since 2018-11-25
 */
public class JWTResponse extends GenericResponse {
	/**
	 * The JWT attached to the response.
	 */
	private String token;

	/**
	 * The main constructor for JWTResponse.
	 * @param token The JWT attached to the response.
	 */
	public JWTResponse(String token) {
		super(Response.Status.OK.toString(), "Token created!");
		this.token = token;
	}

	/**
	 * The constructor for JWTResponse that also modifies details of the superclass GenericResponse.
	 *
	 * @param statusCode The status code of the response.
	 * @param message    The message of the response.
	 * @param token      The JWT attached to the response.
	 */
	public JWTResponse(String statusCode, String message, String token) {
		super(statusCode, message);
		this.token = token;
	}

	/**
	 * Gets the JWT attached to the response.
	 *
	 * @return The JWT attached to the response.
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Sets the JWT attached to the response.
	 *
	 * @param token The new JWT attached to the response.
	 */
	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof JWTResponse)) return false;
		if (!super.equals(o)) return false;
		JWTResponse that = (JWTResponse) o;
		return Objects.equals(token, that.token);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), token);
	}
}
