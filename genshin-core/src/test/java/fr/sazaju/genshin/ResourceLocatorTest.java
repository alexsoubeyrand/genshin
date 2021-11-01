package fr.sazaju.genshin;

import static fr.sazaju.genshin.Enemy.*;
import static fr.sazaju.genshin.Resource.*;
import static fr.sazaju.genshin.Resource.Type.*;
import static fr.sazaju.genshin.ResourceLocator.EnemyPredicate.*;
import static fr.sazaju.genshin.ResourceLocator.ShopPredicate.*;
import static fr.sazaju.genshin.ResourceLocator.ShopPredicate.resourceType;
import static fr.sazaju.genshin.Shop.*;
import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.ResourceLocator.EnemyPredicate;
import fr.sazaju.genshin.ResourceLocator.ShopPredicate;

class ResourceLocatorTest {

	static List<Object> testLocate() {
		return List.of(//
				arguments(Enemy.class, dropping(SLIME_CS), Set.of(SLIMES)), //
				arguments(Enemy.class, dropping(D_MASKS), Set.of(HILICURLS, LAWACURLS, MITACURLS, SAMACURLS)), //
				arguments(Enemy.class, dropping(F_ARROWHEADS), Set.of(HILICURL_SHOOTERS)), //
				arguments(Enemy.class, dropping(D_SCROLLS), Set.of(SAMACURLS)), //
				arguments(Enemy.class, dropping(TH_INSIGNIA), Set.of(TREASURE_HOARDERS)), //
				arguments(Enemy.class, dropping(R_INSIGNIA), Set.of(FATUIS)), //
				arguments(Enemy.class, dropping(W_NECTAR), Set.of(WHOPPERFLOWERS)), //
				arguments(Shop.class, selling(SWEET_FLOWER), Set.of(FLORA)), //
				arguments(Shop.class, selling(SLIME_CS), Set.of()), //
				arguments(Shop.class, selling(CECILIA), Set.of(FLORA)), //
				arguments(Shop.class, selling(WHEAT), Set.of(BLANCHE, DONGSHENG)), //
				arguments(Shop.class, resourceType(COOKED_DISH), Set.of(GOOD_HUNTER)), //
				arguments(Shop.class, resourceType(ENEMIES_DROP), Set.of()) //
		);
	}
	@ParameterizedTest
	@MethodSource
	<T extends Enum<?>> void testLocate(Class<T> enumClass, Predicate<T> predicate, Set<T> expected) {
		assertEquals(expected, new ResourceLocator().locate(enumClass, predicate));
	}

	// TODO : Chercher comment intégrer ce test dans le testLocate
	static List<Arguments> testLocateEnemiesOnResourceType() {
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

	private static ShopPredicate numberOfItemsSold(int numberOfItemsSold) {
		return shop -> shop.getItemsSold().size() == numberOfItemsSold;
	}
	
	@Test
	void testPredicate() {
		Predicate<Object> predicate = new Predicate<Object>() {
			@Override
			public boolean test(Object enumValue) {
				return enumValue.toString().startsWith("F");
			}
		};
		assertEquals(Set.of(FLORA), new ResourceLocator().locate(Shop.class, predicate));
		assertEquals(Set.of(FATUIS), new ResourceLocator().locate(Enemy.class, predicate));
	}
	

	// TODO testLocateShops(Resource.Type type)

//	arguments(Resource.SWEET_FLOWER, Set.of(Shop.FLORA)),
//	arguments(Resource.CECILIA, Set.of(Shop.FLORA)),
//	arguments(Resource.SMALL_LAMP_GRASS, Set.of(Shop.FLORA)),
//	arguments(Resource.CALLA_LILLY, Set.of(Shop.FLORA)),
//	arguments(Resource.WINDWHEEL_ASTER, Set.of(Shop.FLORA))
}
