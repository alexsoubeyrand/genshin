package fr.sazaju.genshin.simulator;

import static fr.sazaju.genshin.simulator.NumberGenerator.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.simulator.NumberGenerator.SplittableNumberGenerator;

class NumberGeneratorTest {

	@Nested
	@TestInstance(Lifecycle.PER_CLASS)
	class FixedNG {
		Stream<Float> valuesInZeroOne() {
			return Stream.of(0f, 0.01f, 0.5f, 0.99f, 1.0f);
		}

		Stream<Float> valuesNotInZeroOne() {
			return Stream.of(-2f, -1f, -0.01f, 1.1f, 2f);
		}

		@ParameterizedTest
		@MethodSource("valuesInZeroOne")
		void testRNGReturnsOnlyProvidedValue(float expectedValue) {
			NumberGenerator rng = createFixedNumberGenerator(expectedValue);
			int sampleSize = 100;
			List<Float> expected = IntStream.range(0, sampleSize)//
					.mapToObj(i -> expectedValue)//
					.collect(toList());
			List<Float> actual = IntStream.range(0, sampleSize)//
					.mapToObj(i -> rng.nextFloat())//
					.collect(toList());
			assertEquals(expected, actual);
		}

		@ParameterizedTest
		@MethodSource("valuesNotInZeroOne")
		void testRNGRejectsValueOutOfZeroOne(float expectedValue) {
			assertThrows(IllegalArgumentException.class, () -> createFixedNumberGenerator(expectedValue));
		}
	}

	@Nested
	@TestInstance(Lifecycle.PER_CLASS)
	class ListNG implements SplittableNGTests<Integer> {
		List<Float> values = List.of(0f, 0.01f, 0.5f, 0.99f, 1.0f);

		@Override
		public Stream<Integer> seeds() {
			return IntStream.range(0, values.size()).mapToObj(i -> i);
		}

		@Override
		public Integer modifySeed(Integer seed) {
			return (seed + 1) % values.size();
		}

		@Override
		public SplittableNumberGenerator<Integer> createRNG(Integer seed) {
			return createListNumberGenerator(values, seed);
		}

		Stream<Float> valuesNotInZeroOne() {
			return Stream.of(-2f, -1f, -0.01f, 1.1f, 2f);
		}

		@Test
		void testRNGReturnsProvidedValues() {
			NumberGenerator rng = createListNumberGenerator(values);
			List<Float> actual = IntStream.range(0, values.size())//
					.mapToObj(i -> rng.nextFloat())//
					.collect(toList());
			assertEquals(values, actual);
		}

		@Test
		void testRNGLoopsOverProvidedValues() {
			NumberGenerator rng = createListNumberGenerator(values);
			int loops = 10;
			List<Float> expected = IntStream.range(0, loops)//
					.mapToObj(i -> values)//
					.flatMap(list -> list.stream())//
					.collect(toList());
			List<Float> actual = IntStream.range(0, expected.size())//
					.mapToObj(i -> rng.nextFloat())//
					.collect(toList());
			assertEquals(expected, actual);
		}

		@Test
		void testRNGRejectsEmptyList() {
			assertThrows(IllegalArgumentException.class, () -> createListNumberGenerator(emptyList()));
		}

		@ParameterizedTest
		@MethodSource("valuesNotInZeroOne")
		void testRNGRejectsValueOutOfZeroOne(float expectedValue) {
			assertThrows(IllegalArgumentException.class, () -> createListNumberGenerator(List.of(expectedValue)));
		}
	}

	@Nested
	@TestInstance(Lifecycle.PER_CLASS)
	class RandomNG implements SplittableNGTests<Long> {

		@Override
		public Stream<Long> seeds() {
			return Stream.of(0L, 1L, 12345L, Long.MIN_VALUE, Long.MAX_VALUE);
		}

		@Override
		public Long modifySeed(Long seed) {
			// Don't play much with extreme seeds, which may have some limits.
			// For example, different extreme seeds may provide the same values.
			return seed + 1;
		}

		@Override
		public SplittableNumberGenerator<Long> createRNG(Long seed) {
			return createRandomNumberGenerator(seed);
		}

		@ParameterizedTest
		@MethodSource("seeds")
		void testRNGReturnsUsuallyDifferentFloats(long seed) {
			int sampleSize = 100;
			SplittableNumberGenerator<Long> rng = createRandomNumberGenerator(seed);
			List<Float> samples = IntStream.range(0, sampleSize)//
					.mapToObj(i -> rng.nextFloat())//
					.collect(toList());
			int distinctSamplesCounter = new HashSet<>(samples).size();
			double miniumDistinctSamples = sampleSize * 0.9;
			assertTrue(distinctSamplesCounter > miniumDistinctSamples, () -> "Too few different values: " + samples);
		}

		@ParameterizedTest
		@MethodSource("seeds")
		void testRNGReturnsFloatsAverageAround50Percent(long seed) {
			int sampleSize = 1000000;
			SplittableNumberGenerator<Long> rng = createRandomNumberGenerator(seed);
			double average = IntStream.range(0, sampleSize)//
					.mapToDouble(i -> rng.nextFloat())//
					.average().getAsDouble();
			double acceptableErrorMargin = 0.001;
			assertEquals(0.5, average, acceptableErrorMargin);
		}

	}

	interface SplittableNGTests<S> {
		Stream<S> seeds();

		S modifySeed(S seed);

		SplittableNumberGenerator<S> createRNG(S seed);

		@ParameterizedTest
		@MethodSource("seeds")
		default void testRNGIsReproducible(S seed) {
			SplittableNumberGenerator<S> refRng = createRNG(seed);
			SplittableNumberGenerator<S> testRng = createRNG(seed);
			int sampleSize = 100;
			List<Float> expected = IntStream.range(0, sampleSize)//
					.mapToObj(i -> refRng.nextFloat())//
					.collect(toList());
			List<Float> actual = IntStream.range(0, sampleSize)//
					.mapToObj(i -> testRng.nextFloat())//
					.collect(toList());
			assertEquals(expected, actual);
		}

		@ParameterizedTest
		@MethodSource("seeds")
		default void testRNGIsSplittable(S startSeed) {
			int sampleSize = 100;
			SplittableNumberGenerator<S> refRng = createRNG(startSeed);
			for (int i = 0; i < sampleSize; i++) {
				refRng.nextFloat();// Consume some samples
			}

			S nextSeed = refRng.getNextSeed();
			SplittableNumberGenerator<S> testRng = createRNG(nextSeed);
			List<Float> expected = IntStream.range(0, sampleSize)//
					.mapToObj(i -> refRng.nextFloat())//
					.collect(toList());
			List<Float> actual = IntStream.range(0, sampleSize)//
					.mapToObj(i -> testRng.nextFloat())//
					.collect(toList());
			assertEquals(expected, actual);
		}

		@ParameterizedTest
		@MethodSource("seeds")
		default void testRNGReturnsFloatsInZeroOne(S seed) {
			int sampleSize = 100;
			SplittableNumberGenerator<S> rng = createRNG(seed);
			List<Float> wrongValues = IntStream.range(0, sampleSize)//
					.mapToObj(i -> rng.nextFloat())//
					.filter(value -> value < 0 || value > 1)//
					.collect(toList());
			assertEquals(0, wrongValues.size(), () -> "Some values are out of [0;1]: " + wrongValues);
		}

		default Stream<Arguments> differentSeeds() {
			return seeds().flatMap(seed -> //
			Stream.of(arguments(seed, modifySeed(seed)))//
			);
		}

		@ParameterizedTest
		@MethodSource("differentSeeds")
		default void testRNGReturnsDifferentFloatsWithDifferentSeeds(S seed1, S seed2) {
			int sampleSize = 1000000;
			SplittableNumberGenerator<S> rng1 = createRNG(seed1);
			SplittableNumberGenerator<S> rng2 = createRNG(seed2);
			long equalCount = IntStream.range(0, sampleSize)//
					.filter(i -> rng1.nextFloat() == rng2.nextFloat())//
					.count();
			assertEquals(0, equalCount);
		}
	}
}
