package fr.sazaju.genshin.service.controller.coder;

import static java.util.Comparator.*;

import java.io.IOException;
import java.util.stream.Stream;

public interface Coder<T, U> {

	U encode(T data) throws IOException;

	T decode(U serial) throws IOException;

	public static <T> String generateShortestSerial(T data, Stream<Coder<T, String>> coders) {
		return coders//
				.map(coder -> {
					try {
						return coder.encode(data);
					} catch (IOException cause) {
						throw new RuntimeException(cause);
					}
				})//
				.sorted(comparing(String::length))//
				.findFirst().orElseThrow(() -> {
					return new RuntimeException("No encoder found for " + data);
				});
	}
}