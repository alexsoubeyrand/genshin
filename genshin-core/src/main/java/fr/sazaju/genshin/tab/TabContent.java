package fr.sazaju.genshin.tab;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import fr.sazaju.genshin.PlayerState;
import fr.sazaju.genshin.item.ItemState;

public class TabContent {

	private final Tab key;
	private final PlayerState data;

	TabContent(Tab key, PlayerState data) {
		this.key = key;
		this.data = data;
	}

	public Stream<Slot> streamSlots() {
		return data.stream()//
				.flatMap(entry -> {
					ItemState<?> item = entry.getItem();
					int quantity = entry.getQuantity();
					if (key.hasSingle(item)) {
						Slot slot = Slot.single(item);
						return IntStream.range(0, quantity).mapToObj(i -> slot);
					} else if (key.hasStack(item)) {
						return Stream.of(Slot.stack(item, quantity));
					} else {
						return Stream.empty();
					}
				});
	}

}
