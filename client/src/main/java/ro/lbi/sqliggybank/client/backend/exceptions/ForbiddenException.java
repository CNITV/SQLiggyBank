package ro.lbi.sqliggybank.client.backend.exceptions;

import lombok.Getter;

public class ForbiddenException extends Exception {

	@Getter
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

}
