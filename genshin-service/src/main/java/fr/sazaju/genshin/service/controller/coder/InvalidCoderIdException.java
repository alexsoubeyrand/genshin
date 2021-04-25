package fr.sazaju.genshin.service.controller.coder;

@SuppressWarnings("serial")
class InvalidCoderIdException extends RuntimeException {
	public InvalidCoderIdException(int expected, int actual) {
		super(String.format("Incorrect coder ID: expected %s but is %s", expected, actual));
	}
}
