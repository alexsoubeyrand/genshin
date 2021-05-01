package fr.sazaju.genshin.service.controller.coder;

import java.util.List;

import fr.sazaju.genshin.simulator.wish.Profile;

public class ProfileDefinition {

	private static final Property<Profile, Integer> consecutiveWishesBelow4Stars = //
			Property.onClass(Integer.class, profile -> profile.consecutiveWishesBelow4Stars);
	private static final Property<Profile, Integer> consecutiveWishesBelow5Stars = //
			Property.onClass(Integer.class, profile -> profile.consecutiveWishesBelow5Stars);
	private static final Property<Profile, Boolean> isExclusiveGuaranteedOnNext5Stars = //
			Property.onClass(Boolean.class, profile -> profile.isExclusiveGuaranteedOnNext5Stars);

	public static final Definition<Profile> V1 = Definition.onProperties(//
			List.of(//
					consecutiveWishesBelow4Stars, //
					consecutiveWishesBelow5Stars, //
					isExclusiveGuaranteedOnNext5Stars//
			), //
			(input) -> new Profile(//
					input.readValue(consecutiveWishesBelow4Stars), //
					input.readValue(consecutiveWishesBelow5Stars), //
					input.readValue(isExclusiveGuaranteedOnNext5Stars)//
			)//
	);
}
