package fr.sazaju.genshin.service.controller.coder;

@SuppressWarnings("serial")
class InvalidCoderVersionException extends RuntimeException {
	public InvalidCoderVersionException(int expectedVersion, int actualVersion) {
		super(String.format("Incorrect coder version: expected %s but is %s", expectedVersion, actualVersion));
	}
}
