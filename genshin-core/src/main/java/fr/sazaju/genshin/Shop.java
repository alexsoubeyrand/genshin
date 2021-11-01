package fr.sazaju.genshin;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

//TODO Prendre en charge les réductions
public enum Shop {
	FLORA("Flora", Map.of(Resource.SWEET_FLOWER, 200, Resource.CECILIA, 1000)), //
	BLANCHE("Blanche", Map.of(Resource.WHEAT, 100, Resource.PEPPER, 80)), //
	DONGSHENG("Dongsheng", Map.of(Resource.WHEAT, 100, Resource.PEPPER, 80, Resource.SHRIMP_MEAT, 120)),
	GOOD_HUNTER("Good Hunter", Map.of(Resource.FLOUR, 150, Resource.TEYVAT_FRIED_EGG, 200, Resource.MINT_JELLY_RECIPE, 1250))

	;
	private String name;
	private Map<Resource, Integer> prices;

	Shop(String name, Map<Resource, Integer> prices) {
		this.prices = prices;
		this.name = name;
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

	public Set<Resource> getItemsSold() {
		return this.prices.keySet();
	}
	
	@Override
	public String toString() {
		return name;
	}

}
