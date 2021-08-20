package fr.sazaju.genshin;

public enum Resource {
	// Enemies drops
	SLIME_CS("Slime Condensates"), //
	D_MASKS("Damaged Masks"), //
	F_ARROWHEADS("Firm Arrowheads"), //
	D_SCROLLS("Divining Scrolls"), //
	TH_INSIGNIA("Treasure Hoarder Insignia"), //
	R_INSIGNIA("Recruit's Insignia"), //
	W_NECTAR("Whopperflower Nectar"), //

	// Natural resources
	SWEET_FLOWER("Sweet Flower"), //
	CECILIA("Cecilia"), //
	SMALL_LAMP_GRASS("Small Lamp Grass"), //
	CALLA_LILLY("Calla Lilly"), //
	WINDWHEEL_ASTER("Windwheel Aster"), //
	CRAB("Crab"), //
	WHEAT("Wheat"), //

	// Sold resources
	PEPPER("Pepper"), //
	SHRIMP_MEAT("Shrimp Meat"), //

	// Crafted ingredients
	FLOUR("Flour"), // Crafted from wheat

	// Cooked dishes
	TEYVAT_FRIED_EGG("Teyvat Fried Egg"), //

	// Recipes
	MINT_JELLY_RECIPE("Recipe: Mint Jelly")

	;

	public enum Type {
		ENEMIES_DROP(), //
		NATURAL_RESOURCE(), //
		SOLD_RESOURCE(), //
		CRAFTED_INGREDIENT(), //
		COOKED_DISH(), //
		RECIPE() //
	}

	private final String name;

	Resource(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
