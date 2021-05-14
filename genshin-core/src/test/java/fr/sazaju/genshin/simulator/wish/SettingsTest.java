package fr.sazaju.genshin.simulator.wish;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.banner.character.Settings;
import fr.sazaju.genshin.banner.character.Settings.Builder;

class SettingsTest {

	// TODO test missing values
	// TODO test invalid values

	@ParameterizedTest
	@MethodSource("validProbabilities")
	void testProbability4StarsProperlySet(double value) {
		assertEquals(value, Settings.build().withProbability4Stars(value).create().probability4Stars);
	}

	@ParameterizedTest
	@MethodSource("invalidProbabilities")
	void testProbability4StarsRejectsInvalidProbability(double value) {
		assertThrows(IllegalArgumentException.class, () -> Settings.build().withProbability4Stars(value));
	}

	@ParameterizedTest
	@MethodSource("validProbabilities")
	void testProbability4StarsWeaponCharacterProperlySet(double value) {
		assertEquals(value,
				Settings.build().withProbability4StarsWeaponCharacter(value).create().probability4StarsWeaponCharacter);
	}

	@ParameterizedTest
	@MethodSource("invalidProbabilities")
	void testProbability4StarsWeaponCharacterRejectsInvalidProbability(double value) {
		assertThrows(IllegalArgumentException.class,
				() -> Settings.build().withProbability4StarsWeaponCharacter(value));
	}

	@ParameterizedTest
	@MethodSource("validProbabilities")
	void testProbability5StarsProperlySet(double value) {
		assertEquals(value, Settings.build().withProbability5Stars(value).create().probability5Stars);
	}

	@ParameterizedTest
	@MethodSource("invalidProbabilities")
	void testProbability5StarsRejectsInvalidProbability(double value) {
		assertThrows(IllegalArgumentException.class, () -> Settings.build().withProbability5Stars(value));
	}

	@ParameterizedTest
	@MethodSource("validProbabilities")
	void testProbability5StarsPermanentExclusiveProperlySet(double value) {
		assertEquals(value, Settings.build().withProbability5StarsPermanentExclusive(value)
				.create().probability5StarsPermanentExclusive);
	}

	@ParameterizedTest
	@MethodSource("invalidProbabilities")
	void testProbability5StarsPermanentExclusiveRejectsInvalidProbability(double value) {
		assertThrows(IllegalArgumentException.class,
				() -> Settings.build().withProbability5StarsPermanentExclusive(value));
	}

	static Collection<Arguments> testProbabilitiesRejectedIf4StarsPlus5StarsExceedsOne() {
		return List.of(//
				Arguments.arguments(0.3, 0.8),//
				Arguments.arguments(0.8, 0.3),//
				Arguments.arguments(0.8, 0.8));
	}
	
	@ParameterizedTest
	@MethodSource
	void testProbabilitiesRejectedIf4StarsPlus5StarsExceedsOne(double proba4Stars, double proba5Stars) {
		Builder builder = Settings.build()//
				.withProbability4Stars(proba4Stars)//
				.withProbability5Stars(proba5Stars);
		assertThrows(IllegalStateException.class, () -> builder.create());
	}

	@ParameterizedTest
	@MethodSource("validGuaranties")
	void testGuaranty4StarsProperlySet(int value) {
		assertEquals(value, Settings.build().withGuaranty4Stars(value).create().guaranty4Stars);
	}

	@ParameterizedTest
	@MethodSource("invalidGuaranties")
	void testGuaranty4StarsRejectsInvalidGuaranty(int value) {
		assertThrows(IllegalArgumentException.class, () -> Settings.build().withGuaranty4Stars(value));
	}

	@ParameterizedTest
	@MethodSource("validGuaranties")
	void testGuaranty5StarsProperlySet(int value) {
		assertEquals(value, Settings.build().withGuaranty5Stars(value).create().guaranty5Stars);
	}

	@ParameterizedTest
	@MethodSource("invalidGuaranties")
	void testGuaranty5StarsRejectsInvalidGuaranty(int value) {
		assertThrows(IllegalArgumentException.class, () -> Settings.build().withGuaranty5Stars(value));
	}

	@Test
	void testMihoyoSettingsAreCorrect() {
		Settings settings = Settings.createMihoyoSettings();
		assertEquals(0.051, settings.probability4Stars);
		assertEquals(0.006, settings.probability5Stars);
		assertEquals(0.5, settings.probability4StarsWeaponCharacter);
		assertEquals(0.5, settings.probability5StarsPermanentExclusive);
		assertEquals(10, settings.guaranty4Stars);
		assertEquals(90, settings.guaranty5Stars);
	}

	static Collection<Double> validProbabilities() {
		return List.of(//
				0.0, // minimum
				0.123, // medium
				1.0);// maximum
	}

	static Collection<Double> invalidProbabilities() {
		return List.of(//
				3.0, // too high
				-0.123);// too low
	}

	static Collection<Integer> validGuaranties() {
		return List.of(//
				1, // minimum
				5, // medium
				10, // medium
				50, // medium
				100, // medium
				Integer.MAX_VALUE);// maximum
	}

	static Collection<Integer> invalidGuaranties() {
		return List.of(//
				0, -1, Integer.MIN_VALUE);// maximum
	}

}
