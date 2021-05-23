package fr.sazaju.genshin.item;

import static fr.sazaju.genshin.Rarity.*;

import fr.sazaju.genshin.Rarity;

public enum ForgingMaterial implements ItemType.WithSingleRarity {
	IRON_CHUNK(NO_RARITY), //
	WHITE_IRON_CHUNK(NO_RARITY), //
	CRYSTAL_CHUNK(NO_RARITY), //
	MAGICAL_CRYSTAL_CHUNK(NO_RARITY), //
	STARSILVER(NO_RARITY), //

	BUTTERFLY_WINGS(NO_RARITY), //
	FROG(NO_RARITY), //
	LIZARD_TAIL(NO_RARITY), //
	CRYSTAL_CORE(NO_RARITY), //
	LUMINESCENT_SPINE(NO_RARITY), //

	FLAMING_FLOWER_STAMEN(NO_RARITY), //
	MIST_FLOWER_COROLLA(NO_RARITY), //
	DANDELION_SEED(NO_RARITY), //
	LOTUS_HEAD(NO_RARITY), //

	ELECTRO_CRYSTAL(NO_RARITY), //
	VITALIZED_DRAGONTOOTH(NO_RARITY), //
	;

	private final Rarity rarity;

	ForgingMaterial(Rarity rarity) {
		this.rarity = rarity;
	}

	@Override
	public Rarity getRarity() {
		return rarity;
	}

	@Override
	public StackableItem<ForgingMaterial> item() {
		return new StackableItem<>(this, getRarity());
	}
}
