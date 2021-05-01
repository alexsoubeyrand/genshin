package fr.sazaju.genshin.simulator.wish;

import static fr.sazaju.genshin.StringReference.*;

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
		int stars = (randomValue < settings.probability5Stars
				|| state.consecutiveWishesBelow5Stars == settings.guaranty5Stars - 1) ? 5 //
						: (randomValue < settings.probability4Stars + settings.probability5Stars
								|| state.consecutiveWishesBelow4Stars == settings.guaranty4Stars - 1) ? 4 //
										: 3;
		Type type = stars == 3 ? Type.WEAPON //
				: stars == 4 && randomValue > settings.probability5Stars
						+ settings.probability4Stars * settings.probability4StarsWeaponCharacter ? Type.WEAPON//
								: Type.CHARACTER;
		boolean isExclusive = stars == 5
				? state.isExclusiveGuaranteedOnNext5Stars
						|| randomValue < settings.probability5Stars * settings.probability5StarsPermanentExclusive
				: false;
		return new Wish(stars, type, isExclusive);
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
