package ro.lbi.sqliggybank.client.backend.exceptions;

import lombok.Getter;

public class NotFoundException extends Exception {

	@Getter
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

}
