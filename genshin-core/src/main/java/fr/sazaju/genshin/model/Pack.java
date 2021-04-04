package fr.sazaju.genshin.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import fr.sazaju.genshin.csv.CsvParser;

public class Pack {

	public final int cristals;
	public final float euros;

	public Pack(int cristals, float euros) {
		this.cristals = cristals;
		this.euros = euros;
	}

	public Pack createFirstOrderVariant() {
		return new Pack(2 * cristals, euros);
	}

	@Override
	public String toString() {
		return String.format("Pack[%s cristals for %s €]", cristals, euros);
	}

	private static final AtomicReference<List<Pack>> packs = new AtomicReference<>();

	public static List<Pack> getAllPacks() {
		return packs.updateAndGet(current -> {
			if (current != null) {
				return current;
			} else {
				InputStream stream = Pack.class.getClassLoader().getResourceAsStream("packs.csv");
				try {
					return new CsvParser().parse(stream, values -> {
						return new Pack(//
								Integer.parseInt(values.get("cristals")), //
								Float.parseFloat(values.get("euros")));
					});
				} catch (IOException cause) {
					throw new RuntimeException("Cannot load packs", cause);
				}
			}
		});
	}
}
