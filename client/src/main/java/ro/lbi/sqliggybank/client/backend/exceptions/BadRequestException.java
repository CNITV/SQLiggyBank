package ro.lbi.sqliggybank.client.backend.exceptions;

public class BadRequestException extends Exception {

	private String title;

	public BadRequestException(String title, String message) {
		this(message);
		this.title = title;
	}

	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException() {
		super();
	}

	public String getTitle() {
		return this.title;
	}
}
