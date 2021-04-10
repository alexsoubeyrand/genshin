package fr.sazaju.genshin.service.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

	private static final Set<HttpStatus> exposedStatus = Set.of(HttpStatus.NOT_FOUND);

	@ExceptionHandler
	protected ResponseEntity<Object> handleAnnotatedException(Exception ex, WebRequest request) throws Exception {
		ResponseStatus annotation = ex.getClass().getAnnotation(ResponseStatus.class);
		if (annotation == null) {
			throw ex;// Let the framework generate an internal error
		}

		HttpStatus status = annotation.value();
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("status", status.value());
		body.put("reason", status.getReasonPhrase());
		if (exposedStatus.contains(status)) {
			body.put("details", ex.getMessage());
		}

		return new ResponseEntity<>(body, status);
	}
}
