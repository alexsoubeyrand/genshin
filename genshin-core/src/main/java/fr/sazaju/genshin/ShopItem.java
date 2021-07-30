package fr.sazaju.genshin;

public enum ShopItem {
	SWEET_FLOWER(ShopTarget.FLORA, 200, 10,"Sweet Flower"), //
	CECILIA(ShopTarget.FLORA, 1000, 5, "Cecilia"), //
	SMALL_LAMP_GRASS(ShopTarget.FLORA, 1000, 5, "Small Lamp Glass"),//
	CALLA_LILLY(ShopTarget.FLORA, 1000, 5, "Calla Lilly"),
	WINDWHEEL_ASTER(ShopTarget.FLORA, 1000, 5, "Windwheel Aster")
	;

	private ShopTarget shop;
	private int price;
	private int stock;

	ShopItem(ShopTarget shop, int price, int stock, String itemName) {
		this.shop = shop;
		this.price = price;
		this.stock = stock;
	}
	
	public ShopTarget getShop() {
		return shop;
	}
}
