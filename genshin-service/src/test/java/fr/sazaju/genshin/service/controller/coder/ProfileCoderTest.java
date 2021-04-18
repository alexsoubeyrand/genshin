package fr.sazaju.genshin.service.controller.coder;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.simulator.wish.Profile;

class ProfileCoderTest implements CollectionCoderTest<Profile> {

	@ParameterizedTest
	@MethodSource("allCodersAndProfilesData")
	public void testEachVersionHasConsistentCoding(//
			ProfileCoder coder, //
			int wishesLessThan4Stars, //
			int wishesLessThan5Stars, //
			boolean isExclusiveGuaranteedOnNext5Stars) throws IOException {

		Profile source = new Profile(wishesLessThan4Stars, wishesLessThan5Stars, isExclusiveGuaranteedOnNext5Stars);
		Profile rebuilt = coder.decode(coder.encode(source));
		assertEquals(wishesLessThan4Stars, rebuilt.wishesLessThan4Stars);
		assertEquals(wishesLessThan5Stars, rebuilt.wishesLessThan5Stars);
		assertEquals(isExclusiveGuaranteedOnNext5Stars, rebuilt.isExclusiveGuaranteedOnNext5Stars);
	}

	@Override
	public Coder<Profile, String> searchSerialCoder(String serial) {
		return ProfileCoder.fromSerial(serial);
	}

	@Override
	public Stream<Coder<Profile, String>> allCoders() {
		return Stream.of(ProfileCoder.values());
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
	public Stream<Profile> allData() {
		return someWishesLessThan4Stars().flatMap(wishesLessThan4Stars -> //
		someWishesLessThan5Stars().flatMap(wishesLessThan5Stars -> //
		allIsExclusiveGuaranteedOnNext5Stars().flatMap(isExclusiveGuaranteedOnNext5Stars -> {//
			return Stream.of(new Profile(//
					wishesLessThan4Stars, //
					wishesLessThan5Stars, //
					isExclusiveGuaranteedOnNext5Stars//
			));
		})));
	}

	public Stream<Arguments> allCodersAndProfilesData() {
		return allCoders().flatMap(version -> //
		someWishesLessThan4Stars().flatMap(wishesLessThan4Stars -> //
		someWishesLessThan5Stars().flatMap(wishesLessThan5Stars -> //
		allIsExclusiveGuaranteedOnNext5Stars().flatMap(isExclusiveGuaranteedOnNext5Stars -> //
		Stream.of(arguments(//
				version, //
				wishesLessThan4Stars, //
				wishesLessThan5Stars, //
				isExclusiveGuaranteedOnNext5Stars//
		))))));
	}
}
