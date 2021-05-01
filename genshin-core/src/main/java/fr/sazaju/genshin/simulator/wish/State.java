package fr.sazaju.genshin.simulator.wish;

import fr.sazaju.genshin.StringUtils;

public class State {
	public final int consecutiveWishesBelow4Stars;
	public final int consecutiveWishesBelow5Stars;
	public final boolean isExclusiveGuaranteedOnNext5Stars;

	public State(//
			int consecutiveWishesBelow4Stars, //
			int consecutiveWishesBelow5Stars, //
			boolean isExclusiveGuaranteedOnNext5Stars) {
		this.consecutiveWishesBelow4Stars = consecutiveWishesBelow4Stars;
		this.consecutiveWishesBelow5Stars = consecutiveWishesBelow5Stars;
		this.isExclusiveGuaranteedOnNext5Stars = isExclusiveGuaranteedOnNext5Stars;
	}

	public static State createFresh() {
		return new State(0, 0, false);
	}

	public State update(Wish wish) {
		int below4Stars = wish.stars == 3 ? this.consecutiveWishesBelow4Stars + 1 : 0;
		int below5Stars = wish.stars == 5 ? 0 : this.consecutiveWishesBelow5Stars + 1;
		boolean exclusiveGuaranteed = wish.stars == 5 && wish.isExclusive ? false//
				: wish.stars == 5 && !wish.isExclusive ? true//
						: this.isExclusiveGuaranteedOnNext5Stars;
		return new State(below4Stars, below5Stars, exclusiveGuaranteed);
	}

	@Override
	public String toString() {
		return StringUtils.toStringFromFields(this);
	}

	static class Builder {
		private int consecutiveWishesBelow4Stars;
		private int consecutiveWishesBelow5Stars;
		private boolean isExclusiveGuaranteedOnNext5Stars;

		public Builder withConsecutiveWishesBelow4Stars(int consecutiveWishesBelow4Stars) {
			this.consecutiveWishesBelow4Stars = consecutiveWishesBelow4Stars;
			return this;
		}

		public Builder withConsecutiveWishesBelow5Stars(int consecutiveWishesBelow5Stars) {
			this.consecutiveWishesBelow5Stars = consecutiveWishesBelow5Stars;
			return this;
		}

		public Builder withExclusiveGuaranteedOnNext5Stars(boolean isExclusiveGuaranteedOnNext5Stars) {
			this.isExclusiveGuaranteedOnNext5Stars = isExclusiveGuaranteedOnNext5Stars;
			return this;
		}

		State build() {
			return new State(consecutiveWishesBelow4Stars, consecutiveWishesBelow5Stars, isExclusiveGuaranteedOnNext5Stars);
		}
	}
}
