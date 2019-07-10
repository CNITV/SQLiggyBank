package ro.lbi.sqliggybank.client.backend.exceptions;

import lombok.Getter;

public class UnauthorizedException extends Exception {

	@Getter
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

}
