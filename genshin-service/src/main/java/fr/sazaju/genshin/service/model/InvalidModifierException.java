package fr.sazaju.genshin.service.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidModifierException extends IllegalArgumentException {

	public InvalidModifierException(Exception cause) {
		super(cause.getMessage());
	}
}
