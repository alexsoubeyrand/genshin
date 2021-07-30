package fr.sazaju.genshin;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
public enum ShopItemSet {
	FLORA(ShopTarget.FLORA.getName());

	private Set<ShopItem> items;
	private String name;

	ShopItemSet(String name) {
		this.name = name;
		this.items = getShopsItems(name);
	}

	public Set<ShopItem> getShopsItems(String shopName) {
		return Stream.of(ShopItem.values())//
				.filter(shopItem -> shopItem.getShop().getName().contains(shopName))//
				.collect(Collectors.toSet());
	}
	
	public Set<ShopItem> getItems() {
		return items;
	}
}
