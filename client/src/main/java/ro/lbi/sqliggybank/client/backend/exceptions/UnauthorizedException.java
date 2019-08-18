package ro.lbi.sqliggybank.client.backend.exceptions;

public class UnauthorizedException extends Exception {

	private String title;

	public UnauthorizedException(String title, String message) {
		this(message);
		this.title = title;
	}

	public UnauthorizedException(String message) {
		super(message);
	}

	public UnauthorizedException() {
		super();
	}

	public String getTitle() {
		return this.title;
	}
}
