package fr.sazaju.genshin.service.controller.coder;

import java.io.IOException;

@SuppressWarnings("serial")
class CannotFindCoderVersionException extends RuntimeException {
	public CannotFindCoderVersionException(int expectedVersion) {
		super(String.format("Expected version %s but cannot be found", expectedVersion));
	}

	public CannotFindCoderVersionException(IOException cause) {
		super("Expected some version but cannot be found", cause);
	}
}
