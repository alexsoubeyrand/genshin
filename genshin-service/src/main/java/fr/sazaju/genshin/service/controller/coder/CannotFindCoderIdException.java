package fr.sazaju.genshin.service.controller.coder;

import java.io.IOException;

@SuppressWarnings("serial")
class CannotFindCoderIdException extends RuntimeException {
	public CannotFindCoderIdException(IOException cause) {
		super("Expected a coder ID but cannot be found", cause);
	}
}
