package fr.sazaju.genshin;

import java.util.Map;
//TODO Prendre en charge les réductions
public enum Shop {
	FLORA(Map.of(Resource.SWEET_FLOWER, 200, Resource.CECILIA, 1000)), //
	BLANCHE(Map.of(Resource.WHEAT, 100, Resource.PEPPER, 80)), //
	DONGSHENG(Map.of(Resource.WHEAT, 100, Resource.PEPPER, 80, Resource.SHRIMP_MEAT, 120)),
	GOOD_HUNTER(Map.of(Resource.FLOUR, 150, Resource.TEYVAT_FRIED_EGG, 200, Resource.MINT_JELLY_RECIPE, 1250))

	;

	private Map<Resource, Integer> prices;

	Shop(Map<Resource, Integer> prices) {
		this.prices = prices;
	}

	boolean sells(Resource resource) {
		return this.prices.keySet().contains(resource);
	}

	Integer getPrice(Resource resource) {
		
		if (prices.containsKey(resource)) {
			return prices.get(resource);
		} else {
			throw new IllegalArgumentException(resource + " isn't sold by " + this);
		}
	}
}
