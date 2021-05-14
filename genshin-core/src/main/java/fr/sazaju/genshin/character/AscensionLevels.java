package fr.sazaju.genshin.character;

import fr.sazaju.genshin.material.AscensionMaterial;
import fr.sazaju.genshin.material.BossDrop;
import fr.sazaju.genshin.material.LocalSpecialty;
import fr.sazaju.genshin.material.MobDrop;

public interface AscensionLevels {

	static AscensionLevels basedOn(AscensionMaterial ascensionMaterial, BossDrop bossDrop, LocalSpecialty localSpecialty, MobDrop commonMaterial) {
		return new AscensionLevels() {
		};
	}

}
