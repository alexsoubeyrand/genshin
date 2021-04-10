package fr.sazaju.genshin.service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UnknownResourceException extends IllegalArgumentException {

	public UnknownResourceException(String resourceDescriptor) {
		super("Unknown " + resourceDescriptor);
	}
}
