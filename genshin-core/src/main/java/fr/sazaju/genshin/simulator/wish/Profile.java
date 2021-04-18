package fr.sazaju.genshin.simulator.wish;

import static fr.sazaju.genshin.StringReference.*;

import java.util.LinkedHashMap;
import java.util.Map;

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
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("<4" + STAR + " count", wishesLessThan4Stars);
		map.put("<5" + STAR + " count", wishesLessThan5Stars);
		map.put("next exclusive", isExclusiveGuaranteedOnNext5Stars);
		return map.toString();
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
