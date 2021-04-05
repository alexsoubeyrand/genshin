package fr.sazaju.genshin.simulator.wish;

public class Settings {
	public final float probability4Stars;
	public final float probability5Stars;
	public final float probability4StarsWeaponCharacter;
	public final float probability5StarsPermanentExclusive;

	public final int guaranty4Stars;
	public final int guaranty5Stars;

	private Settings(//
			float probability4Stars, //
			float probability5Stars, //
			float probability4StarsWeaponCharacter, //
			float probability5StarsPermanentExclusive, //

			int guaranty4Stars, //
			int guaranty5Stars) {
		this.probability4Stars = probability4Stars;
		this.probability5Stars = probability5Stars;
		this.probability4StarsWeaponCharacter = probability4StarsWeaponCharacter;
		this.probability5StarsPermanentExclusive = probability5StarsPermanentExclusive;

		this.guaranty4Stars = guaranty4Stars;
		this.guaranty5Stars = guaranty5Stars;
	}

	public static Settings createMihoyoSettings() {
		return new Settings(.051f, .006f, .5f, .5f, 1, 90);
	}

	public static class Builder {
		private float probability4Stars;
		private float probability5Stars;
		private float probability4StarsWeaponCharacter;
		private float probability5StarsPermanentExclusive;

		private int guaranty4Stars;
		private int guaranty5Stars;

		public Builder withProbability4Stars(float value) {
			this.probability4Stars = value;
			return this;
		}

		public Builder withProbability4StarsWeaponCharacter(float value) {
			this.probability4StarsWeaponCharacter = value;
			return this;
		}

		public Builder withProbability5Stars(float value) {
			this.probability5Stars = value;
			return this;
		}

		public Builder withProbability5StarsPermanentExclusive(float value) {
			this.probability5StarsPermanentExclusive = value;
			return this;
		}

		public Builder withGuaranty4Stars(int value) {
			this.guaranty4Stars = value;
			return this;
		}

		public Builder withGuaranty5Stars(int value) {
			this.guaranty5Stars = value;
			return this;
		}

		public Settings build() {
			return new Settings(probability4Stars, probability5Stars, probability4StarsWeaponCharacter,
					probability5StarsPermanentExclusive, guaranty4Stars, guaranty5Stars);
		}
	}
}
