package fr.sazaju.genshin;

public enum ShopTarget {
	// Case Sweet flower and crab
	// Price ? Stock ? Map sweet flower and flora
	FLORA(Location.MONSTADT, ShopItemSet.FLORA, "Flora");

	private Location location;
	private ShopItemSet shopItemSet;
	private String name;

	ShopTarget(Location location, ShopItemSet shopItemSet, String name) {
		this.location = location;
		this.shopItemSet = shopItemSet;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public ShopItemSet getShopItemSet() {
		return shopItemSet;
	}
}
