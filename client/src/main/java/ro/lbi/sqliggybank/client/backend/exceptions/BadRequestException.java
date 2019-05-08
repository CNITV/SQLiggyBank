package ro.lbi.sqliggybank.client.backend.exceptions;

public class BadRequestException extends Exception {

	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException() {
		super();
	}

}
