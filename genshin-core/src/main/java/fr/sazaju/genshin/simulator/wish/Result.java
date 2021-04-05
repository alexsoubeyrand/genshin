package fr.sazaju.genshin.simulator.wish;

import static fr.sazaju.genshin.StringReference.*;

import fr.sazaju.genshin.StringReference;

public class Result {

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

	public Result(int stars, Type type, boolean isExclusive) {
		this.stars = stars;
		this.type = type;
		this.isExclusive = isExclusive;
	}

	public static Result compute(Settings settings, Memory memory, float randomValue) {
		int stars = (randomValue < settings.probability5Stars
				|| memory.wishesLessThan5Stars == settings.guaranty5Stars - 1) ? 5 //
						: (randomValue < settings.probability4Stars + settings.probability5Stars
								|| memory.wishesLessThan4Stars == settings.guaranty4Stars - 1) ? 4 //
										: 3;
		Type type = stars == 3 ? Type.WEAPON //
				: stars == 4 && randomValue > settings.probability5Stars
						+ settings.probability4Stars * settings.probability4StarsWeaponCharacter ? Type.WEAPON//
								: Type.CHARACTER;
		boolean isExclusive = stars == 5
				? memory.isExclusiveGuaranteedOnNext5Stars
						|| randomValue < settings.probability5Stars * settings.probability5StarsPermanentExclusive
				: false;
		return new Result(stars, type, isExclusive);
	}

	@Override
	public String toString() {
		return (isExclusive ? EXCLUSIVE : PERMANENT) + " " + stars + STAR + " " + type;
	}

	public static class Generator {

		private final Settings settings;
		private Memory memory;

		public Generator(Settings settings, Memory memory) {
			this.settings = settings;
			this.memory = memory;
		}

		public Memory getCurrentMemory() {
			return memory;
		}

		public Result run(float randomValue) {
			Result result = Result.compute(settings, memory, randomValue);
			this.memory = memory.update(result);
			return result;
		}
	}
}
