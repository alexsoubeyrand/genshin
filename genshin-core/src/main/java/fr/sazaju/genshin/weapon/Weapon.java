package fr.sazaju.genshin.weapon;

import fr.sazaju.genshin.StringUtils;

public class Weapon {
	public final WeaponProfile profile;
	public final int weaponLevel;
	public final int ascensionLevel;
	public final int refinementLevel;

	private Weapon(WeaponProfile profile, int weaponLevel, int refinementLevel) {
		this.profile = profile;

		this.weaponLevel = weaponLevel;
		this.ascensionLevel = //
				weaponLevel >= 80 ? 6 //
						: weaponLevel >= 70 ? 5 //
								: weaponLevel >= 60 ? 4 //
										: weaponLevel >= 50 ? 3 //
												: weaponLevel >= 40 ? 2 //
														: weaponLevel >= 20 ? 1 //
																: 0;

		this.refinementLevel = refinementLevel;
	}

	@Override
	public String toString() {
		return StringUtils.toStringFromFields(this);
	}

	public static class Builder {
		private WeaponProfile profile;
		private int weaponLevel;
		private int refinementLevel;

		Builder(WeaponProfile profile) {
			this.profile = profile;
			this.weaponLevel = 1;
			this.refinementLevel = 1;
		}

		public Weapon create() {
			return new Weapon(profile, weaponLevel, refinementLevel);
		}

		public Builder withWeaponLevel(int weaponLevel) {
			this.weaponLevel = weaponLevel;
			return this;
		}

		public Builder withRefinementLevel(int refinementLevel) {
			this.refinementLevel = refinementLevel;
			return this;
		}
	}
}
