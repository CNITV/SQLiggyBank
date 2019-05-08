package ro.lbi.sqliggybank.client.backend.exceptions;

public class NotFoundException extends Exception {

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException() {
		super();
	}

}
