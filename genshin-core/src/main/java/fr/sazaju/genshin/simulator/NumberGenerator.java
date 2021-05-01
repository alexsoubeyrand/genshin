package fr.sazaju.genshin.simulator;

import static java.util.stream.Collectors.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public interface NumberGenerator {

	float nextFloat();

	interface SplittableNumberGenerator<S> extends NumberGenerator {

		S getNextSeed();

	}

	public static NumberGenerator createFixedNumberGenerator(float value) {
		if (value < 0 || value > 1) {
			throw new IllegalArgumentException("Value not in [0;1]: " + value);
		}
		return new NumberGenerator() {

			@Override
			public float nextFloat() {
				return value;
			}
		};
	}

	public static SplittableNumberGenerator<Integer> createListNumberGenerator(List<Float> values, int offset) {
		if (values.isEmpty()) {
			throw new IllegalArgumentException("No value provided");
		}
		List<Float> invalidValues = values.stream()//
				.filter(value -> value < 0 || value > 1)//
				.collect(toList());
		if (!invalidValues.isEmpty()) {
			throw new IllegalArgumentException("Values not in [0;1]: " + invalidValues);
		}
		if (offset < 0 || offset >= values.size()) {
			throw new IllegalArgumentException("Offset must be in [0;" + values.size() + "[: " + offset);
		}
		ArrayList<Float> array = new ArrayList<>(values);
		return new SplittableNumberGenerator<Integer>() {

			int nextIndex = offset;

			@Override
			public float nextFloat() {
				Float value = array.get(nextIndex);
				nextIndex = (nextIndex + 1) % values.size();
				return value;
			}

			@Override
			public Integer getNextSeed() {
				return nextIndex;
			}
		};
	}

	public static SplittableNumberGenerator<Integer> createListNumberGenerator(List<Float> values) {
		return createListNumberGenerator(values, 0);
	}

	public static SplittableNumberGenerator<Long> createRandomNumberGenerator(long seed) {
		Random random = new Random(seed);
		return new SplittableNumberGenerator<>() {

			@Override
			public float nextFloat() {
				return random.nextFloat();
			}

			@Override
			public Long getNextSeed() {
				/*
				 * We read some fields from Random using reflection. Since we access an external
				 * module, we need its authorization (the module must expose these fields in
				 * some way). But it doesn't, so this code generates an illegal reflective
				 * access warning. In future versions of the JDK, this kind of operation should
				 * become forbidden. We need to find a valid way to retrieve the current seed
				 * before that. If we cannot, then we need to use or implement another random
				 * generator which allows us to retrieve the current seed.
				 * 
				 * TODO Find a valid way to retrieve the seed
				 */
				AtomicLong scrambledSeed = extractField(random, "seed");
				long multiplier = extractField(random, "multiplier");
				long nextSeed = scrambledSeed.get() ^ multiplier;
				return nextSeed;
			}

			@SuppressWarnings("unchecked")
			private <T> T extractField(Random random, String name) {
				Field field;
				try {
					field = random.getClass().getDeclaredField(name);
				} catch (NoSuchFieldException | SecurityException cause) {
					throw new RuntimeException(cause);
				}
				field.setAccessible(true);
				try {
					return (T) field.get(random);
				} catch (IllegalArgumentException | IllegalAccessException cause) {
					throw new RuntimeException(cause);
				}
			}
		};
	}
}
