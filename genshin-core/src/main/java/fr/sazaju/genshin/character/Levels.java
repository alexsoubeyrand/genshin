package fr.sazaju.genshin.character;

import static fr.sazaju.genshin.character.Rarity.*;
import static fr.sazaju.genshin.material.EventMaterial.*;
import static fr.sazaju.genshin.material.Mora.*;

import java.util.Map;

import fr.sazaju.genshin.material.Book;
import fr.sazaju.genshin.material.BossDrop;
import fr.sazaju.genshin.material.MobDrop;

public interface Levels {

	Map<Integer, Level> toMap();

	public static Levels fromMap(Map<Integer, Level> levels) {
		return new Levels() {
			@Override
			public Map<Integer, Level> toMap() {
				return levels;
			}
		};
	}

	public static Levels forCharacterTalent(Book book, MobDrop mobDrop, BossDrop bossDrop) {
		return Levels.fromMap(Map.of(//
				2, Level.create(Cost.fromMap(Map.of(//
						THREE_STARS.of(MORA), 12500, //
						TWO_STARS.of(book), 3, //
						ONE_STAR.of(mobDrop), 6//
				))), //
				3, Level.create(Cost.fromMap(Map.of(//
						THREE_STARS.of(MORA), 17500, //
						THREE_STARS.of(book), 2, //
						TWO_STARS.of(mobDrop), 3//
				))), //
				4, Level.create(Cost.fromMap(Map.of(//
						THREE_STARS.of(MORA), 25000, //
						THREE_STARS.of(book), 4, //
						TWO_STARS.of(mobDrop), 4//
				))), //
				5, Level.create(Cost.fromMap(Map.of(//
						THREE_STARS.of(MORA), 30000, //
						THREE_STARS.of(book), 6, //
						TWO_STARS.of(mobDrop), 6//
				))), //
				6, Level.create(Cost.fromMap(Map.of(//
						THREE_STARS.of(MORA), 37500, //
						THREE_STARS.of(book), 9, //
						TWO_STARS.of(mobDrop), 9//
				))), //
				7, Level.create(Cost.fromMap(Map.of(//
						THREE_STARS.of(MORA), 120000, //
						FOUR_STARS.of(book), 4, //
						THREE_STARS.of(mobDrop), 4, //
						FIVE_STARS.of(bossDrop), 1//
				))), //
				8, Level.create(Cost.fromMap(Map.of(//
						THREE_STARS.of(MORA), 260000, //
						FOUR_STARS.of(book), 6, //
						THREE_STARS.of(mobDrop), 6, //
						FIVE_STARS.of(bossDrop), 1//
				))), //
				9, Level.create(Cost.fromMap(Map.of(//
						THREE_STARS.of(MORA), 450000, //
						FOUR_STARS.of(book), 12, //
						THREE_STARS.of(mobDrop), 9, //
						FIVE_STARS.of(bossDrop), 2//
				))), //
				10, Level.create(Cost.fromMap(Map.of(//
						THREE_STARS.of(MORA), 700000, //
						FOUR_STARS.of(book), 16, //
						THREE_STARS.of(mobDrop), 12, //
						FIVE_STARS.of(bossDrop), 2, //
						FIVE_STARS.of(CROWN_OF_INSIGHT), 1//
				))) //
		));
	}
}
