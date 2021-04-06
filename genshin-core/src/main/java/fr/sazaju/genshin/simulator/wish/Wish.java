package fr.sazaju.genshin.simulator.wish;

import java.util.Random;

import fr.sazaju.genshin.simulator.wish.Result.Generator;

public class Wish {

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
		Memory memory = Memory.createFreshMemory();
		Generator generator = new Result.Generator(settings, memory);

		long randomSeed = 0;
		Random randomGenerator = new Random(randomSeed);

		for (int i = 0; i < settings.guaranty5Stars * 2; i++) {
			float randomValue = randomGenerator.nextFloat();
			Result result = generator.run(randomValue);

			System.out.println(String.format("RNGs: %f => %s %s", randomValue, result, generator.getCurrentMemory()));
		}
	}
	
	// TODO Test Memory
	// TODO Test Results
	
	// TODO Compute statistics
	
	// TODO Access through web service

	// TODO refine
	static class Exchange {
		int astrionsPer3StarWeapon;
		int asteriesPer4StarWeapon;
		int asteriesPerFatality;
	}
}
