package fr.sazaju.genshin.service.controller.coder;

import java.util.List;

import fr.sazaju.genshin.simulator.wish.Profile;

public class ProfileDefinition {

	private static final Property<Profile, Integer> wishesLessThan4Stars = //
			Property.onClass(Integer.class, profile -> profile.wishesLessThan4Stars);
	private static final Property<Profile, Integer> wishesLessThan5Stars = //
			Property.onClass(Integer.class, profile -> profile.wishesLessThan5Stars);
	private static final Property<Profile, Boolean> isExclusiveGuaranteedOnNext5Stars = //
			Property.onClass(Boolean.class, profile -> profile.isExclusiveGuaranteedOnNext5Stars);

	public static final Definition<Profile> V1 = Definition.onProperties(//
			List.of(//
					wishesLessThan4Stars, //
					wishesLessThan5Stars, //
					isExclusiveGuaranteedOnNext5Stars//
			), //
			(input) -> new Profile(//
					input.readValue(wishesLessThan4Stars), //
					input.readValue(wishesLessThan5Stars), //
					input.readValue(isExclusiveGuaranteedOnNext5Stars)//
			)//
	);
}
