package fr.sazaju.genshin.simulator.wish;

import fr.sazaju.genshin.simulator.NumberGenerator;
import fr.sazaju.genshin.simulator.wish.Wish.Generator;

public class WishMain {

	public static void main(String[] args) {
//		Settings settings = Settings.createMihoyoSettings();
		Settings settings = Settings.build()//
				.withProbability4Stars(0)//
				.withProbability4StarsWeaponCharacter(0)//
				.withProbability5Stars(0)//
				.withProbability5StarsPermanentExclusive(0)//
				.withGuaranty4Stars(3)//
				.withGuaranty5Stars(5)//
				.create();
		Profile profile = Profile.createFreshProfile();
		Generator generator = new Wish.Generator(settings, profile);

		long randomSeed = 0;
		java.util.Random randomGenerator = new java.util.Random(randomSeed);
		NumberGenerator rng = NumberGenerator.createFixedNumberGenerator(randomGenerator.nextFloat());

		for (int i = 0; i < settings.guaranty5Stars * 2; i++) {
			float randomValue = rng.nextFloat();
			Wish result = generator.nextWish(randomValue);

			System.out.println(String.format("RNGs: %f => %s %s", randomValue, result, generator.getCurrentProfile()));
		}
	}

	// TODO Test Profile
	// TODO Test Results

	// TODO Compute statistics

	// TODO refine
	static class Exchange {
		int astrionsPer3StarWeapon;
		int asteriesPer4StarWeapon;
		int asteriesPerFatality;
	}
}
