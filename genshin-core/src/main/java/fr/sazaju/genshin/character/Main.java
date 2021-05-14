package fr.sazaju.genshin.character;

import java.util.Comparator;
import java.util.Map.Entry;

import fr.sazaju.genshin.material.Material;

public class Main {

	public static void main(String[] args) {
		CharacterProfile character = CharacterProfile.KEQING;
		Levels levels = character.talentLevels;
		for (Entry<Integer, Level> entry : levels.toMap().entrySet()) {
			System.out.println("[level " + entry.getKey() + "]");
			displayCost(entry.getValue().getCost());
		}
		int minIndex = levels.toMap().keySet().stream().mapToInt(i->i).min().orElseThrow();
		int maxIndex = levels.toMap().keySet().stream().mapToInt(i->i).max().orElseThrow();
		System.out.println("[sum " + minIndex + "-" + maxIndex + "]");
		displayCost(levels.toMap().values().stream().map(Level::getCost).reduce(Cost::add).orElse(Cost.empty()));
		// TODO AscensionLevels like TalentLevels
		// TODO Character with current levels and stuff
	}

	private static void displayCost(Cost cost) {
		cost.toMap().entrySet().stream()//
				.sorted(Comparator.comparing(entry -> entry.getKey(), Material.syntaxicComparator()))//
				.forEach(entry -> {
					System.out.println(entry.getKey() + " x" + entry.getValue());
				});
	}

}
