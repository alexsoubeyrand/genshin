package fr.sazaju.genshin;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Resource {
	// Enemies drops
	SLIME_CS("Slime Condensates", Type.ENEMIES_DROP), //
	D_MASKS("Damaged Masks", Type.ENEMIES_DROP), //
	F_ARROWHEADS("Firm Arrowheads", Type.ENEMIES_DROP), //
	D_SCROLLS("Divining Scrolls", Type.ENEMIES_DROP), //
	TH_INSIGNIA("Treasure Hoarder Insignia", Type.ENEMIES_DROP), //
	R_INSIGNIA("Recruit's Insignia", Type.ENEMIES_DROP), //
	W_NECTAR("Whopperflower Nectar", Type.ENEMIES_DROP), //

	// Natural resources
	SWEET_FLOWER("Sweet Flower", Type.NATURAL_RESOURCE), //
	CECILIA("Cecilia", Type.NATURAL_RESOURCE), //
	SMALL_LAMP_GRASS("Small Lamp Grass", Type.NATURAL_RESOURCE), //
	CALLA_LILLY("Calla Lilly", Type.NATURAL_RESOURCE), //
	WINDWHEEL_ASTER("Windwheel Aster", Type.NATURAL_RESOURCE), //
	CRAB("Crab", Type.NATURAL_RESOURCE), //
	WHEAT("Wheat", Type.NATURAL_RESOURCE), //

	// Sold resources
	PEPPER("Pepper", Type.SOLD_RESOURCE), //
	SHRIMP_MEAT("Shrimp Meat", Type.SOLD_RESOURCE), //

	// Crafted ingredients
	FLOUR("Flour", Type.CRAFTED_INGREDIENT), // Crafted from wheat

	// Cooked dishes
	TEYVAT_FRIED_EGG("Teyvat Fried Egg", Type.COOKED_DISH), //

	// Recipes
	MINT_JELLY_RECIPE("Recipe: Mint Jelly", Type.RECIPE)

	;

	public enum Type {
		ENEMIES_DROP(), //
		NATURAL_RESOURCE(), //
		SOLD_RESOURCE(), //
		CRAFTED_INGREDIENT(), //
		COOKED_DISH(), //
		RECIPE() //
		
		;
		
		public Set<Resource> getAllResourcesFromThisType() {
			return Stream.of(Resource.values())
					.filter(resource -> resource.getType().equals(this))
					.collect(Collectors.toSet());
		}
	}

	private final String name;
	private final Resource.Type type;

	Resource(String name, Resource.Type type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public String toString() {
		return name;
	}

	public Resource.Type getType() {
		return type;
	}
	
	

}
