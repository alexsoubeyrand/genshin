package fr.sazaju.genshin.item;

import static fr.sazaju.genshin.Rarity.*;
import static fr.sazaju.genshin.weapon.WeaponCategory.*;

import java.util.stream.Stream;

import fr.sazaju.genshin.Rarity;
import fr.sazaju.genshin.weapon.WeaponCategory;

public enum Billet implements ItemType.WithSingleRarity {
	NORTHLANDER_BOW_BILLET(BOW), //
	NORTHLANDER_CATALYST_BILLET(CATALYST), //
	NORTHLANDER_CLAYMORE_BILLET(CLAYMORE), //
	NORTHLANDER_POLEARM_BILLET(POLEARM), //
	NORTHLANDER_SWORD_BILLET(SWORD);

	private final WeaponCategory weaponCategory;

	Billet(WeaponCategory weaponCategory) {
		this.weaponCategory = weaponCategory;
	}

	@Override
	public Rarity getRarity() {
		return FOUR_STARS;
	}

	@Override
	public StackableItem<Billet> item() {
		return new StackableItem<>(this, getRarity());
	}

	public static Billet selectFor(WeaponCategory category) {
		return Stream.of(values())//
				.filter(billet -> billet.weaponCategory.equals(category))//
				.findFirst().orElseThrow();
	}
}
