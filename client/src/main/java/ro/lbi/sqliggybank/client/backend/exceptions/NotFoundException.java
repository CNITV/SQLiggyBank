package ro.lbi.sqliggybank.client.backend.exceptions;

public class NotFoundException extends Exception {

	private String title;

	public NotFoundException(String title, String message) {
		this(message);
		this.title = title;
	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException() {
		super();
	}

	public String getTitle() {
		return this.title;
	}
}
