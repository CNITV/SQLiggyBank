package ro.lbi.sqliggybank.server.Responses;

import javax.ws.rs.core.Response;
import java.util.Objects;

public class JWTResponse extends GenericResponse {
	private String token;

	public JWTResponse(String token) {
		super(Response.Status.OK.toString(), "Token created!");
		this.token = token;
	}

	public JWTResponse(String statusCode, String message, String token) {
		super(statusCode, message);
		this.token = token;
	}

	public String getToken() {
		return token;
	}

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
