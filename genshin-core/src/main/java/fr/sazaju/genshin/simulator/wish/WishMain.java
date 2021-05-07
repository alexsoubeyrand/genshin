package fr.sazaju.genshin.simulator.wish;

import java.util.stream.Stream;

import fr.sazaju.genshin.StringUtils;
import fr.sazaju.genshin.simulator.NumberGenerator;
import fr.sazaju.genshin.simulator.wish.Wish.Run;
import fr.sazaju.genshin.simulator.wish.Wish.Stats;

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

		State startingState = State.createFresh();

		long randomSeed = 0;
		java.util.Random randomGenerator = new java.util.Random(randomSeed);
		NumberGenerator rng = NumberGenerator.createFixedNumberGenerator(randomGenerator.nextFloat());

		Stream<Run> runsStream = Wish.createStream(settings, startingState, Stream.generate(() -> rng.nextFloat()));
		Stats stats = Wish.computeStats(runsStream, 1000000);
		
		System.out.println(Integer.MAX_VALUE);
		System.out.println(StringUtils.toStringFromFieldsRecursive(stats).replaceAll("[, {}]+", "\n").replaceAll("\n+", "\n").replaceAll("^\n", ""));
	}

	// TODO Test State
	// TODO Test Results

	// TODO Compute statistics

	// TODO refine
	static class Exchange {
		int astrionsPer3StarWeapon;
		int asteriesPer4StarWeapon;
		int asteriesPerFatality;
	}
}
