package fr.sazaju.genshin.character;

import java.util.Objects;

import fr.sazaju.genshin.StringUtils;
import fr.sazaju.genshin.weapon.Weapon;

public class Character {
	public final CharacterProfile profile;
	public final Weapon weapon;
	public final int constellationLevel;
	public final int characterLevel;
	public final int ascensionLevel;
	public final int normalAttackLevel;
	public final int elementalSkillLevel;
	public final int elementalBurstLevel;

	private Character(CharacterProfile profile, Weapon weapon, int constellationLevel, int characterLevel,
			int normalAttackLevel, int elementalSkillLevel, int elementalBurstLevel) {
		this.profile = profile;
		this.weapon = weapon;
		this.constellationLevel = constellationLevel;

		this.characterLevel = characterLevel;
		this.ascensionLevel = //
				characterLevel >= 80 ? 6 //
						: characterLevel >= 70 ? 5 //
								: characterLevel >= 60 ? 4 //
										: characterLevel >= 50 ? 3 //
												: characterLevel >= 40 ? 2 //
														: characterLevel >= 20 ? 1 //
																: 0;

		this.normalAttackLevel = normalAttackLevel;
		this.elementalSkillLevel = elementalSkillLevel;
		this.elementalBurstLevel = elementalBurstLevel;
	}

	@Override
	public String toString() {
		return StringUtils.toStringFromFields(this);
	}

	static class Builder {
		private CharacterProfile profile;
		private Weapon weapon;
		private int constellationLevel;
		private int characterLevel;
		private int normalAttackLevel;
		private int elementalSkillLevel;
		private int elementalBurstLevel;

		Builder(CharacterProfile profile, Weapon weapon) {
			if (!Objects.equals(profile.weaponCategory, weapon.type.category)) {
				throw new IllegalArgumentException(
						String.format("Invalid weapon '%s' of type '%s' for character '%s' which uses '%s'", weapon,
								weapon.type.category, profile, profile.weaponCategory));
			}
			this.profile = profile;
			this.weapon = weapon;
			this.constellationLevel = 0;
			this.characterLevel = 0;
			this.normalAttackLevel = 1;
			this.elementalSkillLevel = 1;
			this.elementalBurstLevel = 1;
		}

		public Character create() {
			return new Character(profile, weapon, constellationLevel, characterLevel, normalAttackLevel,
					elementalSkillLevel, elementalBurstLevel);
		}

		public Builder withNormalAttackLevel(int normalAttackLevel) {
			this.normalAttackLevel = normalAttackLevel;
			return this;
		}

		public Builder withElementalSkillLevel(int elementalSkillLevel) {
			this.elementalSkillLevel = elementalSkillLevel;
			return this;
		}

		public Builder withElementalBurstLevel(int elementalBurstLevel) {
			this.elementalBurstLevel = elementalBurstLevel;
			return this;
		}

		public Builder withCharacterLevel(int characterLevel) {
			this.characterLevel = characterLevel;
			return this;
		}

		public Builder withConstellationLevel(int constellationLevel) {
			this.constellationLevel = constellationLevel;
			return this;
		}
	}
}
