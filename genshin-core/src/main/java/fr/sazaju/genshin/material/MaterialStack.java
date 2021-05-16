package fr.sazaju.genshin.material;

import static fr.sazaju.genshin.material.MaterialStack.Filter.*;
import static fr.sazaju.genshin.material.Mora.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MaterialStack {
	private final Map<Material<?>, Integer> map;

	private MaterialStack(Map<Material<?>, Integer> map) {
		this.map = map;
	}

	public static MaterialStack fromMap(Map<Material<?>, Integer> map) {
		return new MaterialStack(map);
	}

	@Override
	public String toString() {
		return map.toString();
	}

	public Set<Material<?>> getMaterials() {
		return map.keySet();
	}

	public int getQuantity(Material<?> material) {
		return map.getOrDefault(material, 0);
	}

	public MaterialStack addStack(MaterialStack that) {
		return MaterialStack.fromMap(Stream.of(this, that)//
				// Retrieve each unique key
				.map(MaterialStack::getMaterials)//
				.flatMap(Set::stream)//
				.distinct()//
				// Retrieve the value of each key into a map
				.collect(Collectors.toMap(//
						key -> key, //
						key -> this.getQuantity(key) //
								+ that.getQuantity(key)//
				)));
	}

	public MaterialStack addStack(Collection<Entry> stack) {
		return addStack(MaterialStack.fromMap(stack.stream()//
				.collect(Collectors.toMap(Entry::getMaterial, Entry::getQuantity))));
	}

	public MaterialStack addMaterial(Material<?> material, int quantity) {
		return addStack(MaterialStack.fromMap(Map.of(material, quantity)));
	}

	public MaterialStack minusStack(MaterialStack stack) {
		return addStack(stack.times(-1));
	}

	public MaterialStack minusStack(Collection<Entry> stack) {
		return minusStack(MaterialStack.fromMap(stack.stream()//
				.collect(Collectors.toMap(Entry::getMaterial, Entry::getQuantity))));
	}

	public MaterialStack minusMaterial(Material<?> material, int quantity) {
		return minusStack(MaterialStack.fromMap(Map.of(material, quantity)));
	}

	public MaterialStack times(int multiplier) {
		return MaterialStack.fromMap(map.entrySet().stream()//
				.map(entry -> Map.entry(entry.getKey(), entry.getValue() * multiplier))//
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
	}

	public MaterialStack filter(Filter filter) {
		return MaterialStack.fromMap(map.entrySet().stream()//
				.filter(entry -> filter.test(entry.getKey(), entry.getValue()))//
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
	}

	public boolean contains(MaterialStack stack) {
		// TODO What about negative quantities?
		return map.entrySet().stream()//
				.allMatch(entry -> {
					Material<?> material = entry.getKey();
					int quantity = entry.getValue();
					int currentQuantity = getQuantity(material);
					return currentQuantity >= quantity;
				});
	}

	public static interface RecipeStrategy {
		public static final RecipeStrategy BEST_EFFORT = (originalHistory, currentHistory,
				targetQuantities) -> currentHistory;
		public static final RecipeStrategy ONLY_IF_SUCCESSFUL = (originalHistory, currentHistory, targetQuantities) -> {
			for (Material<?> material : targetQuantities.getMaterials()) {
				if (currentHistory.getResultingStack().getQuantity(material) < targetQuantities.getQuantity(material)) {
					// Cannot reach target, don't touch anything
					// TODO Best effort? (as much as we can, even if incomplete)
					return originalHistory;
				}
			}
			return currentHistory;
		};

		MaterialStackHistory validate(MaterialStackHistory originalHistory, MaterialStackHistory currentHistory,
				MaterialStack targetQuantities);
	}

	public MaterialStackHistory createRecipeHistory(MaterialStack targetQuantities) {
		return createRecipeHistory(targetQuantities, RecipeStrategy.ONLY_IF_SUCCESSFUL);
	}

	public MaterialStackHistory createRecipeHistory(MaterialStack targetQuantities, RecipeStrategy strategy) {
		return createRecipeHistory(MaterialStackHistory.from(this), targetQuantities, strategy);
	}

	private static MaterialStackHistory createRecipeHistory(MaterialStackHistory originalHistory,
			MaterialStack targetQuantities, RecipeStrategy strategy) {
		MaterialStackHistory currentHistory = originalHistory;
		boolean isUpdated;
		do {
			isUpdated = false;
			for (Material<?> material : targetQuantities.getMaterials()) {
				int targetQuantity = targetQuantities.getQuantity(material);
				MaterialStackHistory nextHistory = fillMaterialByConversions(currentHistory, material, targetQuantity,
						strategy);
				isUpdated = isUpdated || !nextHistory.equals(currentHistory);
				currentHistory = nextHistory;
			}
		} while (isUpdated);
		return strategy.validate(originalHistory, currentHistory, targetQuantities);
	}

	private static MaterialStackHistory fillMaterialByConversions(MaterialStackHistory history, Material<?> material,
			int targetQuantity, RecipeStrategy strategy) {
		int currentQuantity = history.getResultingStack().getQuantity(material);
		if (targetQuantity <= currentQuantity) {
			// Nothing to fill
			return history;
		} else {
			int requiredQuantity = targetQuantity - currentQuantity;
			Map<MaterialStack, MaterialStackHistory> recipesResult = new HashMap<>();
			for (MaterialStack recipe : material.getConversionRecipes()) {
				// TODO Consider cases where we produce more than one material at a time
				// Example: enhancement ore forging
				MaterialStack conversions = recipe.times(requiredQuantity);
				MaterialStack requiredQuantities = conversions.filter(strictlyNegative()).times(-1);
				MaterialStackHistory recipeHistory = createRecipeHistory(history, requiredQuantities, strategy);
				if (recipeHistory.getResultingStack().contains(requiredQuantities)) {
					recipesResult.put(recipe, recipeHistory.appendDiff(conversions));
				} else {
					// Cannot reach target, ignore recipe
				}
			}
			// TODO Return best result instead of first one
			return recipesResult.isEmpty() ? history : recipesResult.values().iterator().next();
		}
	}

	public static MaterialStack empty() {
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
			return materials(stack.getMaterials());
		}

	}

	public Stream<Entry> stream() {
		return map.entrySet().stream().map(entry -> entry(entry.getKey(), entry.getValue()));
	}

	public static interface Entry {
		Material<?> getMaterial();

		int getQuantity();
	}

	public static Entry entry(Material<?> material, int quantity) {
		return new Entry() {

			@Override
			public Material<?> getMaterial() {
				return material;
			}

			@Override
			public int getQuantity() {
				return quantity;
			}
		};
	}

}
