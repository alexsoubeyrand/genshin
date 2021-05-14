package fr.sazaju.genshin.material;

import static fr.sazaju.genshin.material.MaterialStack.Filter.*;
import static fr.sazaju.genshin.material.Mora.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.sazaju.genshin.Rarity;

public interface MaterialStack {
	Map<Material<?>, Integer> toMap();

	public static MaterialStack fromMap(Map<Material<?>, Integer> stack) {
		return new MaterialStack() {
			@Override
			public Map<Material<?>, Integer> toMap() {
				return stack;
			}

			@Override
			public String toString() {
				return stack.toString();
			}
		};
	}

	default MaterialStack add(MaterialStack that) {
		Map<Material<?>, Integer> map1 = this.toMap();
		Map<Material<?>, Integer> map2 = that.toMap();
		Map<Material<?>, Integer> aggregate = Stream.of(map1, map2)//
				.map(Map::keySet)//
				.flatMap(Set::stream)//
				.distinct()//
				.collect(Collectors.toMap(//
						key -> key, //
						key -> map1.getOrDefault(key, 0) //
								+ map2.getOrDefault(key, 0)//
				));
		return MaterialStack.fromMap(aggregate);
	}

	default MaterialStack add(Map<Material<?>, Integer> stack) {
		return add(MaterialStack.fromMap(stack));
	}

	default MaterialStack add(Material<?> material, int quantity) {
		return add(Map.of(material, quantity));
	}

	default MaterialStack times(int multiplier) {
		return MaterialStack.fromMap(this.toMap().entrySet().stream()//
				.map(entry -> Map.entry(entry.getKey(), entry.getValue() * multiplier))//
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
	}

	default MaterialStack minus(MaterialStack stack) {
		return add(stack.toMap().entrySet().stream()//
				.map(entry -> Map.entry(entry.getKey(), -entry.getValue()))//
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
	}

	default MaterialStack filter(Filter filter) {
		return MaterialStack.fromMap(this.toMap().entrySet().stream()//
				.filter(entry -> filter.test(entry.getKey(), entry.getValue()))//
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
	}

	default MaterialStack fillWithConversions() {
		// TODO Best effort conversion?
		MaterialStack currentStack = this;
		Map<Material<?>, Integer> map = this.toMap();
		Material<?> material = Rarity.FOUR_STARS.of(CharacterAscensionMaterial.VAJRADA);
		int negativeQuantity = map.get(material);
		currentStack = search(currentStack, material, negativeQuantity);
		// TODO Search for all negative values (don't compute twice the same)
		return currentStack;
	}

	// TODO Refactor
	default MaterialStack search(MaterialStack currentStack, Material<?> material, int negativeQuantity) {
		int requiredValue = -negativeQuantity;
		System.out.println(">>>");
		System.out.println("Require: " + material + " x" + requiredValue);
		Optional<MaterialStack> conversionRecipe = material.getConversionRecipe();
		if (conversionRecipe.isPresent()) {
			MaterialStack recipe = conversionRecipe.get();
			System.out.println("Recipe: " + recipe);
			MaterialStack singleConsumption = recipe.filter(strictlyNegative());
			System.out.println("Single consumption: " + singleConsumption);
			MaterialStack requiredStack = singleConsumption.times(negativeQuantity);
			System.out.println("Require: " + requiredStack);
			if (!currentStack.contains(requiredStack)) {
				System.out.println("Not enough, search recursively");
				Set<Material<?>> materials = requiredStack.toMap().keySet();
				MaterialStack adaptedStack = currentStack;
				for (Material<?> materialToFill : materials) {
					int requiredQuantity = requiredStack.getQuantity(materialToFill);
					int currentQuantity = currentStack.getQuantity(materialToFill);
					adaptedStack = search(adaptedStack, materialToFill, currentQuantity - requiredQuantity);
					if (adaptedStack.getQuantity(materialToFill) < requiredQuantity) {
						System.out.println("Cannot fill");
						return currentStack;
					}
					System.out.println("Adapted: " + adaptedStack);
				}
				currentStack = adaptedStack;
			}
			currentStack = currentStack.minus(requiredStack).add(material, requiredValue);
		}
		System.out.println("<<<");
		return currentStack;
	}

	default int getQuantity(Material<?> material) {
		return this.toMap().getOrDefault(material, 0);
	}

	default boolean contains(MaterialStack stack) {
		// TODO What about negative quantities?
		return stack.toMap().entrySet().stream()//
				.allMatch(entry -> {
					Material<?> material = entry.getKey();
					int quantity = entry.getValue();
					int currentQuantity = getQuantity(material);
					return currentQuantity >= quantity;
				});
	}

	static MaterialStack empty() {
		return MaterialStack.fromMap(Collections.emptyMap());
	}

	public static interface Filter {
		boolean test(Material<?> material, int quantity);

		default Filter and(Filter otherFilter) {
			return (material, quantity) -> this.test(material, quantity) && otherFilter.test(material, quantity);
		}

		default Filter or(Filter otherFilter) {
			return (material, quantity) -> this.test(material, quantity) || otherFilter.test(material, quantity);
		}

		public static Filter nonZero() {
			return (material, quantity) -> quantity != 0;
		}

		public static Filter strictlyPositive() {
			return (material, quantity) -> quantity > 0;
		}

		public static Filter strictlyNegative() {
			return (material, quantity) -> quantity < 0;
		}

		public static Filter noMora() {
			return (material, quantity) -> !material.type.equals(MORA);
		}

		public static Filter materials(Collection<Material<?>> materials) {
			return (material, quantity) -> materials.contains(material);
		}

		public static Filter materials(Material<?>... materials) {
			return materials(Set.of(materials));
		}

		public static Filter materialsIn(MaterialStack stack) {
			return materials(stack.toMap().keySet());
		}

	}
}
