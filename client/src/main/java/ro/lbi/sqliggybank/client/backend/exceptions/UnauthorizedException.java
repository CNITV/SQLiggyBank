package ro.lbi.sqliggybank.client.backend.exceptions;

public class UnauthorizedException extends Exception {

	public UnauthorizedException(String message) {
		super(message);
	}

	public UnauthorizedException() {
		super();
	}

}
