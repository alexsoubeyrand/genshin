package fr.sazaju.genshin.service.controller.coder;

import java.util.List;

import fr.sazaju.genshin.simulator.wish.Settings;

public class SettingsDefinition {

	private static final Property<Settings, Double> probability4Stars = //
			new Property<>(Double.class, settings -> settings.probability4Stars);
	private static final Property<Settings, Double> probability4StarsWeaponCharacter = //
			new Property<>(Double.class, settings -> settings.probability4StarsWeaponCharacter);
	private static final Property<Settings, Double> probability5Stars = //
			new Property<>(Double.class, settings -> settings.probability5Stars);
	private static final Property<Settings, Double> probability5StarsPermanentExclusive = //
			new Property<>(Double.class, settings -> settings.probability5StarsPermanentExclusive);
	private static final Property<Settings, Integer> guaranty4Stars = //
			new Property<>(Integer.class, settings -> settings.guaranty4Stars);
	private static final Property<Settings, Integer> guaranty5Stars = //
			new Property<>(Integer.class, settings -> settings.guaranty5Stars);

	public static final Definition<Settings> V1 = new Definition<>(//
			List.of(//
					probability4Stars, //
					probability4StarsWeaponCharacter, //
					probability5Stars, //
					probability5StarsPermanentExclusive, //
					guaranty4Stars, //
					guaranty5Stars//
			), (input) -> Settings.build()//
					.withProbability4Stars(input.readValue(probability4Stars))//
					.withProbability4StarsWeaponCharacter(input.readValue(probability4StarsWeaponCharacter))//
					.withProbability5Stars(input.readValue(probability5Stars))//
					.withProbability5StarsPermanentExclusive(input.readValue(probability5StarsPermanentExclusive))//
					.withGuaranty4Stars(input.readValue(guaranty4Stars))//
					.withGuaranty5Stars(input.readValue(guaranty5Stars))//
					.create()//
	);
}
