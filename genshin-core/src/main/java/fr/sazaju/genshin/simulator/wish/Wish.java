package fr.sazaju.genshin.simulator.wish;

import static fr.sazaju.genshin.StringReference.*;
import static java.util.Comparator.*;

import java.util.Comparator;
import java.util.stream.Stream;

import fr.sazaju.genshin.StringReference;

public class Wish {

	enum Type {
		WEAPON(StringReference.WEAPON), CHARACTER(StringReference.CHARACTER);

		private final StringReference ref;

		Type(StringReference ref) {
			this.ref = ref;
		}

		@Override
		public String toString() {
			return ref.toString();
		}
	}

	public final int stars;
	public final Type type;
	public final boolean isExclusive;

	public Wish(int stars, Type type, boolean isExclusive) {
		this.stars = stars;
		this.type = type;
		this.isExclusive = isExclusive;
	}

	public static Wish compute(Settings settings, State state, float randomValue) {
		Wish guaranteedWish = computeGuaranteedWish(settings, state, randomValue);
		Wish randomWish = computeRandomWish(settings, state, randomValue);
		Comparator<Wish> stars = comparing(wish -> wish.stars);
		Comparator<Wish> exclusivity = comparing(wish -> wish.isExclusive);
		Comparator<Wish> comparator = stars.thenComparing(exclusivity);
		return comparator.compare(randomWish, guaranteedWish) < 0 ? guaranteedWish : randomWish;
	}

	private static Wish computeRandomWish(Settings settings, State state, float randomValue) {
		int stars = randomValue < settings.probability5Stars ? 5 //
				: randomValue < settings.probability4Stars + settings.probability5Stars ? 4 //
						: 3;

		Type type = stars == 3 || stars == 4 && randomValue > settings.probability5Stars
				+ settings.probability4Stars * settings.probability4StarsWeaponCharacter ? Type.WEAPON //
						: Type.CHARACTER;

		boolean isExclusive = stars == 5 && (state.isExclusiveGuaranteedOnNext5Stars
				|| randomValue < settings.probability5Stars * settings.probability5StarsPermanentExclusive);

		return new Wish(stars, type, isExclusive);
	}

	private static Wish computeGuaranteedWish(Settings settings, State state, float randomValue) {
		if (state.consecutiveWishesBelow5Stars + 1 >= settings.guaranty5Stars) {
			if (state.isExclusiveGuaranteedOnNext5Stars || randomValue < settings.probability5StarsPermanentExclusive) {
				return new Wish(5, Type.CHARACTER, true);
			} else {
				return new Wish(5, Type.CHARACTER, false);
			}
		} else if (state.consecutiveWishesBelow4Stars + 1 >= settings.guaranty4Stars) {
			if (randomValue < settings.probability4StarsWeaponCharacter) {
				return new Wish(4, Type.CHARACTER, false);
			} else {
				return new Wish(4, Type.WEAPON, false);
			}
		} else {
			return new Wish(3, Type.WEAPON, false);
		}
	}

	@Override
	public String toString() {
		return (isExclusive ? EXCLUSIVE : PERMANENT) + " " + stars + STAR + " " + type;
	}

	public static class Run {
		public final State previousState;
		public final Float randomValue;
		public final Wish wish;
		public final State nextState;

		Run(State previousState, Float randomValue, Wish wish, State nextState) {
			this.previousState = previousState;
			this.randomValue = randomValue;
			this.wish = wish;
			this.nextState = nextState;
		};
	}

	public static Stream<Run> createStream(Settings settings, State startingState, Stream<Float> randomStream) {
		State[] state = { startingState };
		return randomStream.map(randomValue -> {
			State previousState = state[0];
			Wish wish = Wish.compute(settings, previousState, randomValue);
			State nextState = previousState.update(wish);

			state[0] = nextState;
			return new Run(previousState, randomValue, wish, nextState);
		});
	}
}
