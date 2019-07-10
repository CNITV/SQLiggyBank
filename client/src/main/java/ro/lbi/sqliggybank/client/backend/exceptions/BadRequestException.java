package ro.lbi.sqliggybank.client.backend.exceptions;

import lombok.Getter;

public class BadRequestException extends Exception {

	@Getter
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

}
