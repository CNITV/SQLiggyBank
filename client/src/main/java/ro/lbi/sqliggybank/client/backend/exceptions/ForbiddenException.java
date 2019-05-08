package ro.lbi.sqliggybank.client.backend.exceptions;

public class ForbiddenException extends Exception {

	public ForbiddenException(String message) {
		super(message);
	}

	public ForbiddenException() {
		super();
	}

}
