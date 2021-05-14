package fr.sazaju.genshin.service.controller.coder;

import java.util.List;

import fr.sazaju.genshin.banner.character.State;

public class StateDefinition {

	private static final Property<State, Integer> consecutiveWishesBelow4Stars = //
			Property.onClass(Integer.class, state -> state.consecutiveWishesBelow4Stars);
	private static final Property<State, Integer> consecutiveWishesBelow5Stars = //
			Property.onClass(Integer.class, state -> state.consecutiveWishesBelow5Stars);
	private static final Property<State, Boolean> isExclusiveGuaranteedOnNext5Stars = //
			Property.onClass(Boolean.class, state -> state.isExclusiveGuaranteedOnNext5Stars);

	public static final Definition<State> V1 = Definition.onProperties(//
			List.of(//
					consecutiveWishesBelow4Stars, //
					consecutiveWishesBelow5Stars, //
					isExclusiveGuaranteedOnNext5Stars//
			), //
			(input) -> new State(//
					input.readValue(consecutiveWishesBelow4Stars), //
					input.readValue(consecutiveWishesBelow5Stars), //
					input.readValue(isExclusiveGuaranteedOnNext5Stars)//
			)//
	);
}
