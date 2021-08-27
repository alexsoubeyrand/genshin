package fr.sazaju.genshin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.AssertionFailedError;

import com.sun.jdi.connect.Connector.Argument;

class ResourceLocatorTest {

	static List<Object> testLocateEnemies() {
		return List.of(//
				arguments(Resource.SLIME_CS, Set.of(Enemy.SLIMES)), //
				arguments(Resource.D_MASKS, Set.of(Enemy.HILICURLS, Enemy.LAWACURLS, Enemy.MITACURLS, Enemy.SAMACURLS)), //
				arguments(Resource.F_ARROWHEADS, Set.of(Enemy.HILICURL_SHOOTERS)), //
				arguments(Resource.D_SCROLLS, Set.of(Enemy.SAMACURLS)), //
				arguments(Resource.TH_INSIGNIA, Set.of(Enemy.TREASURE_HOARDERS)), //
				arguments(Resource.R_INSIGNIA, Set.of(Enemy.FATUIS)), //
				arguments(Resource.W_NECTAR, Set.of(Enemy.WHOPPERFLOWERS)) //
				);
	}
	
	@ParameterizedTest
	@MethodSource
	void testLocateEnemies(Resource resource, Set<Enemy> expected) {
		assertEquals(expected, new ResourceLocator().locateEnemies(resource));
	}
	
	@Test
	void testLocateShops() {
		assertEquals(Set.of(Shop.FLORA), new ResourceLocator().locateShops(Resource.SWEET_FLOWER));
	}
	
	@Test
	void testLocateShops2() {
		assertEquals(Set.of(), new ResourceLocator().locateShops(Resource.SLIME_CS));
	}
	
	@Test
	void testLocateShops3() {
		assertEquals(Set.of(Shop.FLORA), new ResourceLocator().locateShops(Resource.CECILIA));
	}
	
	@Test
	void testLocateBlanche() {
		assertEquals(Set.of(Shop.BLANCHE, Shop.DONGSHENG), new ResourceLocator().locateShops(Resource.WHEAT));
	}
	
	@Test
	void testLocateCookedDishes() {
		assertEquals(Set.of(Shop.GOOD_HUNTER), new ResourceLocator().locateShops(Resource.Type.COOKED_DISH));
	}
	
	@Test
	void testLocateEnemiesDrop() {
		assertEquals(Set.of(), new ResourceLocator().locateShops(Resource.Type.ENEMIES_DROP));
	}
	
	// TODO testLocateShops(Resource.Type type)
	
//	arguments(Resource.SWEET_FLOWER, Set.of(Shop.FLORA)),
//	arguments(Resource.CECILIA, Set.of(Shop.FLORA)),
//	arguments(Resource.SMALL_LAMP_GRASS, Set.of(Shop.FLORA)),
//	arguments(Resource.CALLA_LILLY, Set.of(Shop.FLORA)),
//	arguments(Resource.WINDWHEEL_ASTER, Set.of(Shop.FLORA))
}
