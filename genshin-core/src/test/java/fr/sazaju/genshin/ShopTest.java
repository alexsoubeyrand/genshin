package fr.sazaju.genshin;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ShopTest {

	@Test
	void test() {
		assertEquals(true, Shop.FLORA.sells(Resource.SWEET_FLOWER));
	}

// Rajouter des cas de tests (un magasin existant et un nouveau magasin)
	static List<Arguments> supportedPrices() {
		return List.of(//
				arguments(Shop.FLORA, Resource.SWEET_FLOWER, 200), //
				arguments(Shop.FLORA, Resource.CECILIA, 1000), //
				arguments(Shop.BLANCHE, Resource.WHEAT, 100), //
				arguments(Shop.BLANCHE, Resource.PEPPER, 80), //
				arguments(Shop.DONGSHENG, Resource.WHEAT, 100), //
				arguments(Shop.DONGSHENG, Resource.PEPPER, 80), //
				arguments(Shop.DONGSHENG, Resource.SHRIMP_MEAT, 120), //
				arguments(Shop.GOOD_HUNTER, Resource.FLOUR, 150), //
				arguments(Shop.GOOD_HUNTER, Resource.TEYVAT_FRIED_EGG, 200), //
				arguments(Shop.GOOD_HUNTER, Resource.MINT_JELLY_RECIPE, 1250) //
		);
	}

	static List<Arguments> unsupportedPrices() {
		List<Arguments> supportedPrices = supportedPrices();
		final Map<Shop, List<Resource>> map = new HashMap<>();
		for (Arguments arguments : supportedPrices) {
			Object[] valeurs = arguments.get();
			Shop shop = (Shop) valeurs[0];
			map.computeIfAbsent(shop, s -> new LinkedList<>());
			map.get(shop).add((Resource) valeurs[1]);

		}

		List<Arguments> testCases = new LinkedList<>();
		for (Shop shop : Shop.values()) {
			for (Resource resource : Resource.values()) {
				List<Resource> list = map.get(shop);
				boolean contains = list.contains(resource);
				if (!contains) {
					testCases.add(arguments(shop, resource));
				} else {
					// Supported case : ignore
				}
			}
		}

		return testCases;
	}

	@ParameterizedTest(name = "{0} sells {1} at {2}")
	@MethodSource("supportedPrices")
	void testGetPriceOnSupportedResourceReturnsCorrectPrice(Shop shop, Resource resource, int expected) {
		assertEquals(expected, shop.getPrice(resource));
	}

	@ParameterizedTest(name = "{0} doesn''t sell {1}")
	@MethodSource("unsupportedPrices")
	void testGetPriceOnUnsupportedResourceThrowsException(Shop shop, Resource resource) {

		var exception = assertThrows(IllegalArgumentException.class, () -> shop.getPrice(resource));
		assertEquals(resource + " isn't sold by " + shop, exception.getMessage());
	}

}
