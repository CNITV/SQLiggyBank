package ro.lbi.sqliggybank.client.backend.exceptions;

public class ForbiddenException extends Exception {

	private String title;

	public ForbiddenException(String title, String message) {
		this(message);
		this.title = title;
	}

	public ForbiddenException(String message) {
		super(message);
	}

	public ForbiddenException() {
		super();
	}

	public String getTitle() {
		return this.title;
	}
}
