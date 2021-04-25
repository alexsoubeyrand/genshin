package fr.sazaju.genshin.simulator.wish;

import fr.sazaju.genshin.StringUtils;

public class Profile {
	public final int wishesLessThan4Stars;
	public final int wishesLessThan5Stars;
	public final boolean isExclusiveGuaranteedOnNext5Stars;

	public Profile(//
			int wishesLessThan4Stars, //
			int wishesLessThan5Stars, //
			boolean isExclusiveGuaranteedOnNext5Stars) {
		this.wishesLessThan4Stars = wishesLessThan4Stars;
		this.wishesLessThan5Stars = wishesLessThan5Stars;
		this.isExclusiveGuaranteedOnNext5Stars = isExclusiveGuaranteedOnNext5Stars;
	}

	public static Profile createFreshProfile() {
		return new Profile(0, 0, false);
	}

	public Profile update(Wish wish) {
		int wishesLessThan4Stars = wish.stars == 3 ? this.wishesLessThan4Stars + 1 : 0;
		int wishesLessThan5Stars = wish.stars == 5 ? 0 : this.wishesLessThan5Stars + 1;
		boolean isExclusiveGuaranteedOnNext5Stars = wish.stars == 5 && wish.isExclusive ? false//
				: wish.stars == 5 && !wish.isExclusive ? true//
						: this.isExclusiveGuaranteedOnNext5Stars;
		return new Profile(wishesLessThan4Stars, wishesLessThan5Stars, isExclusiveGuaranteedOnNext5Stars);
	}

	@Override
	public String toString() {
		return StringUtils.toStringFromFields(this);
	}

	static class Builder {
		private int wishesLessThan4Stars;
		private int wishesLessThan5Stars;
		private boolean isExclusiveGuaranteedOnNext5Stars;

		public Builder withWishesLessThan4Stars(int wishesLessThan4Stars) {
			this.wishesLessThan4Stars = wishesLessThan4Stars;
			return this;
		}

		public Builder withWishesLessThan5Stars(int wishesLessThan5Stars) {
			this.wishesLessThan5Stars = wishesLessThan5Stars;
			return this;
		}

		public Builder withExclusiveGuaranteedOnNext5Stars(boolean isExclusiveGuaranteedOnNext5Stars) {
			this.isExclusiveGuaranteedOnNext5Stars = isExclusiveGuaranteedOnNext5Stars;
			return this;
		}

		Profile build() {
			return new Profile(wishesLessThan4Stars, wishesLessThan5Stars, isExclusiveGuaranteedOnNext5Stars);
		}
	}
}
