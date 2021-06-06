package fr.sazaju.genshin;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(Lifecycle.PER_CLASS)
public interface EqualHashCodeTest<T> {
	public static class Comparison<T> {
		public final T tested;
		public final Object compared;
		public final boolean expected;

		public Comparison(T tested, Object compared, boolean expected) {
			this.tested = tested;
			this.compared = compared;
			this.expected = expected;
		}

		@Override
		public String toString() {
			return String.format("%s equals %s => %s", tested, compared, expected);
		}
	}

	Stream<Comparison<T>> equalityComparisons();

	@ParameterizedTest
	@MethodSource("equalityComparisons")
	default void testEquals(Comparison<?> c) {
		assertEquals(c.expected, c.tested.equals(c.compared));
	}

	default Stream<Comparison<T>> testHashCode() {
		// Consider only equal cases
		return equalityComparisons().filter(comparison -> comparison.expected);
	}

	@ParameterizedTest
	@MethodSource
	default void testHashCode(Comparison<?> c) {
		assertEquals(c.tested.hashCode(), c.compared.hashCode());
	}
}
