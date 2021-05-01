package fr.sazaju.genshin.simulator.wish;

import static fr.sazaju.genshin.StringReference.*;

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

	public static Wish compute(Settings settings, Profile profile, float randomValue) {
		int stars = (randomValue < settings.probability5Stars
				|| profile.consecutiveWishesBelow5Stars == settings.guaranty5Stars - 1) ? 5 //
						: (randomValue < settings.probability4Stars + settings.probability5Stars
								|| profile.consecutiveWishesBelow4Stars == settings.guaranty4Stars - 1) ? 4 //
										: 3;
		Type type = stars == 3 ? Type.WEAPON //
				: stars == 4 && randomValue > settings.probability5Stars
						+ settings.probability4Stars * settings.probability4StarsWeaponCharacter ? Type.WEAPON//
								: Type.CHARACTER;
		boolean isExclusive = stars == 5
				? profile.isExclusiveGuaranteedOnNext5Stars
						|| randomValue < settings.probability5Stars * settings.probability5StarsPermanentExclusive
				: false;
		return new Wish(stars, type, isExclusive);
	}

	@Override
	public String toString() {
		return (isExclusive ? EXCLUSIVE : PERMANENT) + " " + stars + STAR + " " + type;
	}

	public static class Generator {

		private final Settings settings;
		private Profile profile;

		public Generator(Settings settings, Profile profile) {
			this.settings = settings;
			this.profile = profile;
		}

		public Profile getCurrentProfile() {
			return profile;
		}

		public Wish nextWish(float randomValue) {
			Wish wish = Wish.compute(settings, profile, randomValue);
			this.profile = profile.update(wish);
			return wish;
		}
	}
}
