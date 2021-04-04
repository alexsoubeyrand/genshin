package fr.sazaju.genshin.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class PackTest {

	@Test
	void testPackStoresCorrectCristals() {
		assertEquals(5, new Pack(5, 0).cristals);
	}

	@Test
	void testPackStoresCorrectEuros() {
		assertEquals(5, new Pack(0, 5).euros);
	}

	@Test
	void testFirstOrderVariantHasTwiceCristals() {
		assertEquals(10, new Pack(5, 0).createFirstOrderVariant().cristals);
	}

	@Test
	void testFirstOrderVariantHasSameEuros() {
		assertEquals(3, new Pack(0, 3).createFirstOrderVariant().euros);
	}
	
	@Test
	void testToStringContainsCristals() {
		assertTrue(new Pack(123, 0).toString().contains("123"));
	}
	
	@Test
	void testToStringContainsEuros() {
		assertTrue(new Pack(0, 123).toString().contains("123"));
	}

	@Test
	void testAllPacksAreCorrect() {
		List<Pack> actual = Pack.getAllPacks();

		List<Pack> expected = List.of(//
				new Pack(60, 1.09f), //
				new Pack(300, 5.49f), //
				new Pack(980, 16.99f), //
				new Pack(1980, 32.99f), //
				new Pack(3280, 54.99f), //
				new Pack(6480, 109.99f));

		/*
		 * A pack does not have an identity, so we cannot implement a reliable
		 * comparison through equals(). We create instead an equivalent, but complete,
		 * String representation of each pack, which makes them comparable.
		 */
		Function<List<Pack>, List<String>> formatter = packs -> packs.stream()//
				.map(pack -> String.format("%s:%s", pack.cristals, pack.euros))//
				.collect(Collectors.toList());

		assertEquals(formatter.apply(expected), formatter.apply(actual));
	}
	
	@Test
	void testAllPacksRemainTheSame() {
		List<Pack> expected = Pack.getAllPacks();
		List<Pack> actual = Pack.getAllPacks();

		/*
		 * A pack does not have an identity, so we cannot implement a reliable
		 * comparison through equals(). We create instead an equivalent, but complete,
		 * String representation of each pack, which makes them comparable.
		 */
		Function<List<Pack>, List<String>> formatter = packs -> packs.stream()//
				.map(pack -> String.format("%s:%s", pack.cristals, pack.euros))//
				.collect(Collectors.toList());

		assertEquals(formatter.apply(expected), formatter.apply(actual));
	}
}
