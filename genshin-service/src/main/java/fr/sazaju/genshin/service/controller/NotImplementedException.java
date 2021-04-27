package fr.sazaju.genshin.service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
public class NotImplementedException extends IllegalArgumentException {

	public NotImplementedException() {
		super("Not yet implemented");
	}
}
