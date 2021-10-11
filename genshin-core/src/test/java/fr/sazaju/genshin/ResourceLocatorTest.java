package fr.sazaju.genshin;

import static fr.sazaju.genshin.Resource.CECILIA;
import static fr.sazaju.genshin.Resource.SLIME_CS;
import static fr.sazaju.genshin.Resource.SWEET_FLOWER;
import static fr.sazaju.genshin.Resource.WHEAT;
import static fr.sazaju.genshin.Resource.Type.*;
import static fr.sazaju.genshin.Resource.Type.ENEMIES_DROP;
import static fr.sazaju.genshin.ResourceLocator.EnemyPredicate.dropping;
import static fr.sazaju.genshin.ResourceLocator.ShopPredicate.resourceType;
import static fr.sazaju.genshin.ResourceLocator.ShopPredicate.selling;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.ResourceLocator.EnemyPredicate;
import fr.sazaju.genshin.ResourceLocator.ShopPredicate;

class ResourceLocatorTest {

	//Generaliser pour mettre des shops dans les arguments et adapter la méthode "testLocate"
	static List<Object> testLocate() {
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
	void testLocate(Resource resource, Set<Enemy> expected) {
		assertEquals(expected, new ResourceLocator().locate(Enemy.class, dropping(resource)));
	}
	
	
	static List <Arguments> testLocateEnemiesOnResourceType() {
		List<Arguments> list = new LinkedList<>();
		for (Resource.Type type : Resource.Type.values()) {
			list.add(arguments(type, type.equals(ENEMIES_DROP) ? Set.of(Enemy.values()) : emptySet()));
		}
		return list;
	}
	
	@ParameterizedTest
	@MethodSource
	void testLocateEnemiesOnResourceType(Resource.Type type, Set<Enemy> expected) {
		assertEquals(expected, new ResourceLocator().locate(Enemy.class, EnemyPredicate.resourceType(type)));
	}

	@Test
	void testLocateShops() {
		ResourceLocator r = new ResourceLocator();
		assertEquals(Set.of(Shop.FLORA), r.locate(Shop.class, selling(SWEET_FLOWER)));
	}

	@Test
	void testLocateShops2() {
		ResourceLocator r = new ResourceLocator();
		assertEquals(Set.of(), r.locate(Shop.class, selling(SLIME_CS)));
	}

	@Test
	void testLocateShops3() {
		ResourceLocator r = new ResourceLocator();
		assertEquals(Set.of(Shop.FLORA), r.locate(Shop.class, selling(CECILIA)));
	}

	@Test
	void testLocateBlanche() {
		ResourceLocator r = new ResourceLocator();
		assertEquals(Set.of(Shop.BLANCHE, Shop.DONGSHENG), r.locate(Shop.class, selling(WHEAT)));
	}

	@Test
	void testLocateCookedDishes() {
		ResourceLocator r = new ResourceLocator();
		assertEquals(Set.of(Shop.GOOD_HUNTER), r.locate(Shop.class, resourceType(COOKED_DISH)));
	}

	@Test
	void testLocateEnemiesDrop() {
		ResourceLocator r = new ResourceLocator();
		assertEquals(Set.of(), r.locate(Shop.class, resourceType(ENEMIES_DROP)));
	}

	public interface testInterface {
		public void doSomething();
	}

	public class testClass implements testInterface {
		public void doSomething() {
			System.out.println("Hello World !");
		}
	}
	
	public class testClass2 implements testInterface {

		@Override
		public void doSomething() {
			System.out.println("Bonjour monde !");
			
		}
		
	}

	@Test
	void test() {
		ResourceLocator r = new ResourceLocator();
		assertEquals(Set.of(Shop.BLANCHE, Shop.FLORA), r.locate(Shop.class, numberOfItemsSold(2)));
	}

	private ShopPredicate numberOfItemsSold(int numberOfItemsSold) {
		return shop -> shop.getItemsSold().size() == numberOfItemsSold;
	}
	
	

	// TODO testLocateShops(Resource.Type type)

//	arguments(Resource.SWEET_FLOWER, Set.of(Shop.FLORA)),
//	arguments(Resource.CECILIA, Set.of(Shop.FLORA)),
//	arguments(Resource.SMALL_LAMP_GRASS, Set.of(Shop.FLORA)),
//	arguments(Resource.CALLA_LILLY, Set.of(Shop.FLORA)),
//	arguments(Resource.WINDWHEEL_ASTER, Set.of(Shop.FLORA))
}
