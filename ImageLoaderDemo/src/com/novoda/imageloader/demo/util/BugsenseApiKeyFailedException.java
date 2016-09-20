package com.novoda.imageloader.demo.util;

public class BugsenseApiKeyFailedException extends Exception {

	private final String message;

	public BugsenseApiKeyFailedException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
