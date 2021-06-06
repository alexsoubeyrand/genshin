package fr.sazaju.genshin.tab;

import static java.util.Collections.*;

import java.util.HashSet;
import java.util.Set;

import fr.sazaju.genshin.PlayerState;
import fr.sazaju.genshin.item.ItemState;
import fr.sazaju.genshin.item.ItemType;
import fr.sazaju.genshin.item.artifact.Artifact;
import fr.sazaju.genshin.item.simple.Billet;
import fr.sazaju.genshin.item.simple.BossDrop;
import fr.sazaju.genshin.item.simple.CharacterAscensionMaterial;
import fr.sazaju.genshin.item.simple.CommonAscensionMaterial;
import fr.sazaju.genshin.item.simple.EliteCommonAscensionMaterial;
import fr.sazaju.genshin.item.simple.EnhancementOre;
import fr.sazaju.genshin.item.simple.EventMaterial;
import fr.sazaju.genshin.item.simple.Food;
import fr.sazaju.genshin.item.simple.ForgingMaterial;
import fr.sazaju.genshin.item.simple.Gadget;
import fr.sazaju.genshin.item.simple.LocalSpecialty;
import fr.sazaju.genshin.item.simple.Potion;
import fr.sazaju.genshin.item.simple.TalentLevelUpMaterial;
import fr.sazaju.genshin.item.simple.WeaponAscensionMaterial;
import fr.sazaju.genshin.item.weapon.Weapon;

public enum Tab {
	WEAPONS(//
			singles(Weapon.class), //
			stacks(EnhancementOre.class)//
	), //
	ARTIFACTS(//
			singles(Artifact.class)//
	), //
	ASCENSION_MATERIALS(//
			stacks(BossDrop.class), //
			stacks(CharacterAscensionMaterial.class), //
			stacks(CommonAscensionMaterial.class), //
			stacks(EliteCommonAscensionMaterial.class), //
			stacks(EventMaterial.class), //
			stacks(TalentLevelUpMaterial.class), //
			stacks(WeaponAscensionMaterial.class)//
	), //
	FOOD(//
			stacks(Food.class), //
			stacks(Potion.class)//
	), //
	MATERIALS(//
			stacks(Billet.class), //
			stacks(ForgingMaterial.class), //
			stacks(LocalSpecialty.class)//
	), //
	GADGETS(//
			stacks(Gadget.class)//
	), //
	QUEST_ITEMS(stacks(null)), // TODO
	PRECIOUS_ITEMS(stacks(null)), // TODO
	FURNISHINGS(stacks(null)), // TODO
	;

	public final Set<Class<? extends ItemState<?>>> singles;
	public final Set<Class<? extends ItemType>> stacks;

	Tab(SlotDefinition... slotsDefinitions) {
		Set<Class<? extends ItemState<?>>> singles = new HashSet<>();
		Set<Class<? extends ItemType>> stacks = new HashSet<>();
		for (SlotDefinition definition : slotsDefinitions) {
			definition.feed(singles, stacks);
		}
		this.singles = unmodifiableSet(singles);
		this.stacks = unmodifiableSet(stacks);
	}

	public boolean hasSingle(ItemState<?> item) {
		return singles.contains(item.getClass());
	}

	public boolean hasStack(ItemState<?> item) {
		return stacks.contains(item.getType().getClass());
	}

	public TabContent on(PlayerState data) {
		return new TabContent(this, data);
	}

	private interface SlotDefinition {
		void feed(Set<Class<? extends ItemState<?>>> singles, Set<Class<? extends ItemType>> stacks);
	}

	// TODO Item or ItemType?
	private static SlotDefinition singles(Class<? extends ItemState<?>> itemClass) {
		return (singles, stacks) -> singles.add(itemClass);
	}

	// TODO Item or ItemType?
	private static SlotDefinition stacks(Class<? extends ItemType> typeClass) {
		return (singles, stacks) -> stacks.add(typeClass);
	}
}
