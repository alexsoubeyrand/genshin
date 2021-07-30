package fr.sazaju.genshin;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ShopTest {

	@Test
	void test() {
		assertEquals(true, Shop.FLORA.sells(Resource.SWEET_FLOWER));
	}

	@Test
	void testPrice() {
		assertEquals(200, Shop.FLORA.getPrice(Resource.SWEET_FLOWER));
	}

	@Test
	void testPrice2() {
		assertEquals(1000, Shop.FLORA.getPrice(Resource.CECILIA));
	}

	@Test
	void testPrice3() {
		Resource resource = Resource.TH_INSIGNIA;
		Shop shop = Shop.FLORA;
		var exception = assertThrows(IllegalArgumentException.class, () -> shop.getPrice(resource));
		assertEquals(resource + " isn't sold by " + shop, exception.getMessage());
	}
	
	@Test
	void testPrice4() {
		assertEquals(100, Shop.BLANCHE.getPrice(Resource.WHEAT));
	}
}
