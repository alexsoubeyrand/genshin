package fr.sazaju.genshin.service.controller.coder;

import java.util.List;

import fr.sazaju.genshin.banner.character.Settings;

public class SettingsDefinition {

	private static final Property<Settings, Double> probability4Stars = //
			Property.onClass(Double.class, settings -> settings.probability4Stars);
	private static final Property<Settings, Double> probability4StarsWeaponCharacter = //
			Property.onClass(Double.class, settings -> settings.probability4StarsWeaponCharacter);
	private static final Property<Settings, Double> probability5Stars = //
			Property.onClass(Double.class, settings -> settings.probability5Stars);
	private static final Property<Settings, Double> probability5StarsPermanentExclusive = //
			Property.onClass(Double.class, settings -> settings.probability5StarsPermanentExclusive);
	private static final Property<Settings, Integer> guaranty4Stars = //
			Property.onClass(Integer.class, settings -> settings.guaranty4Stars);
	private static final Property<Settings, Integer> guaranty5Stars = //
			Property.onClass(Integer.class, settings -> settings.guaranty5Stars);

	public static final Definition<Settings> V1 = Definition.onProperties(//
			List.of(//
					probability4Stars, //
					probability4StarsWeaponCharacter, //
					probability5Stars, //
					probability5StarsPermanentExclusive, //
					guaranty4Stars, //
					guaranty5Stars//
			), //
			(input) -> Settings.build()//
					.withProbability4Stars(input.readValue(probability4Stars))//
					.withProbability4StarsWeaponCharacter(input.readValue(probability4StarsWeaponCharacter))//
					.withProbability5Stars(input.readValue(probability5Stars))//
					.withProbability5StarsPermanentExclusive(input.readValue(probability5StarsPermanentExclusive))//
					.withGuaranty4Stars(input.readValue(guaranty4Stars))//
					.withGuaranty5Stars(input.readValue(guaranty5Stars))//
					.create()//
	);
}
