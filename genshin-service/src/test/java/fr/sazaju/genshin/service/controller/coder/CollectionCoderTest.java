package fr.sazaju.genshin.service.controller.coder;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(Lifecycle.PER_CLASS)
interface CollectionCoderTest<T> {

	default Stream<Arguments> testEachCoderIsCorrectlyMappedToProducedSerial() {
		return allCoders().flatMap(coder -> //
		allData().flatMap(data -> //
		Stream.of(arguments(//
				coder, //
				data//
		))));
	}

	@ParameterizedTest
	@MethodSource
	default void testEachCoderIsCorrectlyMappedToProducedSerial(Coder<T, String> coder, T data) throws IOException {
		String serial = coder.encode(data);
		assertEquals(coder, searchSerialCoder(serial));
	}

	default Stream<Arguments> testEachCoderRejectsDecodingSerialProducedByOtherCoder() {
		return allCoders().flatMap(coder1 -> //
		allCoders().flatMap(coder2 -> {
			if (coder1 == coder2) {
				return Stream.empty();
			} else {
				return allData().flatMap(data -> //
				Stream.of(arguments(//
						coder1, //
						coder2, //
						data)));
			}
		}));
	}

	@ParameterizedTest
	@MethodSource
	default void testEachCoderRejectsDecodingSerialProducedByOtherCoder(//
			Coder<T, String> coder1, //
			Coder<T, String> coder2, //
			T data) throws IOException {

		String serial = coder1.encode(data);
		assertThrows(InvalidCoderVersionException.class, () -> coder2.decode(serial));
	}

	Stream<Coder<T, String>> allCoders();

	Coder<T, String> searchSerialCoder(String serial);

	Stream<T> allData();
}
