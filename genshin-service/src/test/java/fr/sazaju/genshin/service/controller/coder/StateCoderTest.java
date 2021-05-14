package fr.sazaju.genshin.service.controller.coder;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.banner.character.State;

class StateCoderTest implements CollectionCoderTest<State> {

	@ParameterizedTest
	@MethodSource("allCodersAndStatesData")
	public void testEachVersionHasConsistentCoding(//
			StateCoder coder, //
			int wishesLessThan4Stars, //
			int wishesLessThan5Stars, //
			boolean isExclusiveGuaranteedOnNext5Stars) throws IOException {

		State source = new State(wishesLessThan4Stars, wishesLessThan5Stars, isExclusiveGuaranteedOnNext5Stars);
		State rebuilt = coder.decode(coder.encode(source));
		assertEquals(wishesLessThan4Stars, rebuilt.consecutiveWishesBelow4Stars);
		assertEquals(wishesLessThan5Stars, rebuilt.consecutiveWishesBelow5Stars);
		assertEquals(isExclusiveGuaranteedOnNext5Stars, rebuilt.isExclusiveGuaranteedOnNext5Stars);
	}

	@Override
	public Coder<State, String> searchSerialCoder(String serial) {
		return StateCoder.fromSerial(serial);
	}

	@Override
	public Stream<Coder<State, String>> allCoders() {
		return Stream.of(StateCoder.values());
	}

	public Stream<Integer> someWishesLessThan4Stars() {
		return Stream.of(0, 1, 5, 10, Integer.MAX_VALUE);
	}

	public Stream<Integer> someWishesLessThan5Stars() {
		return Stream.of(0, 1, 5, 10, 90, Integer.MAX_VALUE);
	}

	public Stream<Boolean> allIsExclusiveGuaranteedOnNext5Stars() {
		return Stream.of(false, true);
	}

	@Override
	public Stream<State> allData() {
		return someWishesLessThan4Stars().flatMap(wishesLessThan4Stars -> //
		someWishesLessThan5Stars().flatMap(wishesLessThan5Stars -> //
		allIsExclusiveGuaranteedOnNext5Stars().flatMap(isExclusiveGuaranteedOnNext5Stars -> {//
			return Stream.of(new State(//
					wishesLessThan4Stars, //
					wishesLessThan5Stars, //
					isExclusiveGuaranteedOnNext5Stars//
			));
		})));
	}

	public Stream<Arguments> allCodersAndStatesData() {
		return allCoders().flatMap(coder -> //
		someWishesLessThan4Stars().flatMap(wishesLessThan4Stars -> //
		someWishesLessThan5Stars().flatMap(wishesLessThan5Stars -> //
		allIsExclusiveGuaranteedOnNext5Stars().flatMap(isExclusiveGuaranteedOnNext5Stars -> //
		Stream.of(arguments(//
				coder, //
				wishesLessThan4Stars, //
				wishesLessThan5Stars, //
				isExclusiveGuaranteedOnNext5Stars//
		))))));
	}
}
