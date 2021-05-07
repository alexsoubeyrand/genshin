package fr.sazaju.genshin.simulator.wish;

import static fr.sazaju.genshin.StringReference.*;
import static java.util.Comparator.*;

import java.util.Comparator;
import java.util.function.Function;
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

	public static class Stats {
		class Data {
			public final int counter;
			public final float rate;
			public final int averageRunsToObtain;

			public Data(int counterData, int counterTotal) {
				this.counter = counterData;
				this.rate = (float) (((double) counterData) / counterRuns);
				this.averageRunsToObtain = counterData == 0 ? 0 : counterRuns / counterData;
			}
		}

		public final int counterRuns;
		public final Data runs3Stars;
		public final Data runs4Stars;
		public final Data runs4StarsWeapons;
		public final Data runs4StarsCharacters;
		public final Data runs5Stars;
		public final Data runs5StarsPermanents;
		public final Data runs5StarsExclusives;

		public Stats(//
				int counter3Stars, //
				int counter4StarsWeapons, //
				int counter4StarsCharacters, //
				int counter5StarsPermanents, //
				int counter5StarsExclusives//
		) {
			this.counterRuns = counter3Stars + counter4StarsWeapons + counter4StarsCharacters + counter5StarsPermanents
					+ counter5StarsExclusives;
			
			this.runs3Stars = new Data(counter3Stars, counterRuns);
			this.runs4StarsWeapons = new Data(counter4StarsWeapons, counterRuns);
			this.runs4StarsCharacters = new Data(counter4StarsCharacters, counterRuns);
			this.runs5StarsPermanents = new Data(counter5StarsPermanents, counterRuns);
			this.runs5StarsExclusives = new Data(counter5StarsExclusives, counterRuns);
			this.runs4Stars = new Data(counter4StarsWeapons + counter4StarsCharacters, counterRuns);
			this.runs5Stars = new Data(counter5StarsPermanents + counter5StarsExclusives, counterRuns);
		}

		private static class Collector {
			int counter3Stars;
			int counter4StarsWeapons;
			int counter4StarsCharacters;
			int counter5StarsPermanents;
			int counter5StarsExclusives;

			public void collect(Wish wish) {
				if (wish.stars == 3) {
					counter3Stars++;
				} else if (wish.stars == 4) {
					if (wish.type == Type.WEAPON) {
						counter4StarsWeapons++;
					} else if (wish.type == Type.CHARACTER) {
						counter4StarsCharacters++;
					} else {
						throw new RuntimeException("Unmanaged type: " + wish.type);
					}
				} else if (wish.stars == 5) {
					if (wish.isExclusive) {
						counter5StarsExclusives++;
					} else {
						counter5StarsPermanents++;
					}
				} else {
					throw new RuntimeException("Unmanaged stars: " + wish.stars);
				}
			}

			public void merge(Collector collector) {
				this.counter3Stars += collector.counter3Stars;
				this.counter4StarsWeapons += collector.counter4StarsWeapons;
				this.counter4StarsCharacters += collector.counter4StarsCharacters;
				this.counter5StarsPermanents += collector.counter5StarsPermanents;
				this.counter5StarsExclusives += collector.counter5StarsExclusives;
			}

			public Stats enrichStats() {
				return new Stats(//
						counter3Stars, //
						counter4StarsWeapons, //
						counter4StarsCharacters, //
						counter5StarsPermanents, //
						counter5StarsExclusives//
				);
			}
		}
	}

	public static Stats computeStats(Stream<Run> runsStream, int runsCount) {
		return runsStream//
				.limit(runsCount)//
				.map(run -> run.wish)//
				.collect(Stats.Collector::new, Stats.Collector::collect, Stats.Collector::merge)//
				.enrichStats();
	}
}
