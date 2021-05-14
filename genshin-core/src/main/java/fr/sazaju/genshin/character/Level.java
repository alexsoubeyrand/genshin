package fr.sazaju.genshin.character;

public interface Level {

	Cost getCost();

	public static Level create(Cost cost) {
		return new Level() {

			@Override
			public Cost getCost() {
				return cost;
			}

		};
	}

}
