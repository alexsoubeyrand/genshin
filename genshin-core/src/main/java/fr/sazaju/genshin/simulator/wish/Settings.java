package fr.sazaju.genshin.simulator.wish;

import static fr.sazaju.genshin.StringReference.*;

import fr.sazaju.genshin.StringUtils;

public class Settings {
	public final double probability4Stars;
	public final double probability5Stars;
	public final double probability4StarsWeaponCharacter;
	public final double probability5StarsPermanentExclusive;

	public final int guaranty4Stars;
	public final int guaranty5Stars;

	private Settings(//
			double probability4Stars, //
			double probability5Stars, //
			double probability4StarsWeaponCharacter, //
			double probability5StarsPermanentExclusive, //

			int guaranty4Stars, //
			int guaranty5Stars) {
		this.probability4Stars = probability4Stars;
		this.probability5Stars = probability5Stars;
		this.probability4StarsWeaponCharacter = probability4StarsWeaponCharacter;
		this.probability5StarsPermanentExclusive = probability5StarsPermanentExclusive;

		this.guaranty4Stars = guaranty4Stars;
		this.guaranty5Stars = guaranty5Stars;
	}

	@Override
	public String toString() {
		return StringUtils.toStringFromFields(this);
	}

	public static Settings createMihoyoSettings() {
		return new Settings(.051, .006, .5, .5, 10, 90);
	}

	public static Builder build() {
		return new Builder();
	}

	public static class Builder {
		private double probability4Stars;
		private double probability5Stars;
		private double probability4StarsWeaponCharacter;
		private double probability5StarsPermanentExclusive;

		private int guaranty4Stars;
		private int guaranty5Stars;

		private Builder() {
			// Private constructor, use factory method(s) instead
		}

		public Builder withProbability4Stars(double value) {
			validateProbability(value);
			this.probability4Stars = value;
			return this;
		}

		public Builder withProbability4StarsWeaponCharacter(double value) {
			validateProbability(value);
			this.probability4StarsWeaponCharacter = value;
			return this;
		}

		public Builder withProbability5Stars(double value) {
			validateProbability(value);
			this.probability5Stars = value;
			return this;
		}

		public Builder withProbability5StarsPermanentExclusive(double value) {
			validateProbability(value);
			this.probability5StarsPermanentExclusive = value;
			return this;
		}

		public Builder withGuaranty4Stars(int value) {
			validateGuaranty(value);
			this.guaranty4Stars = value;
			return this;
		}

		public Builder withGuaranty5Stars(int value) {
			validateGuaranty(value);
			this.guaranty5Stars = value;
			return this;
		}

		private void validateProbability(double value) {
			if (value < 0 || value > 1) {
				throw new IllegalArgumentException("A probability should be in [0;1]");
			}
		}

		private void validateGuaranty(int value) {
			if (value < 1) {
				throw new IllegalArgumentException("A guaranty should be >0");
			}
		}

		public Settings create() {
			if (probability4Stars + probability5Stars > 1) {
				throw new IllegalStateException(
						"4" + STAR + " and 5" + STAR + " probabilities must sum up to a value in [0;1]");
			}
			return new Settings(probability4Stars, probability5Stars, probability4StarsWeaponCharacter,
					probability5StarsPermanentExclusive, guaranty4Stars, guaranty5Stars);
		}
	}
}
