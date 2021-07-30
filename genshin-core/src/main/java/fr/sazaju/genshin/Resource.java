package fr.sazaju.genshin;

public enum Resource {

	SLIME_CS("Slime Condensates"), //
	D_MASKS("Damaged Masks"), //
	F_ARROWHEADS("Firm Arrowheads"), //
	D_SCROLLS("Divining Scrolls"), //
	TH_INSIGNIA("Treasure Hoarder Insignia"), //
	R_INSIGNIA("Recruit's Insignia"), //
	W_NECTAR("Whopperflower Nectar"), //
	SWEET_FLOWER("Sweet Flower"), //
	CECILIA("Cecilia"), //
	SMALL_LAMP_GRASS("Small Lamp Grass"), //
	CALLA_LILLY("Calla Lilly"), //
	WINDWHEEL_ASTER("Windwheel Aster"), //
	CRAB("Crab"), //
	WHEAT("Wheat")
	
	;
	

	private final String name;

	Resource(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
