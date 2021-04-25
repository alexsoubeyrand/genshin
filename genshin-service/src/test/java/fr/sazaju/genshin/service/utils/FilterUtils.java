package fr.sazaju.genshin.service.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class FilterUtils {
	public static <T> Predicate<T> onItemsProvidingNewFieldValues() {
		Map<String, Set<Object>> observedValues = new HashMap<>();
		BiFunction<Field, Object, Boolean> alreadyObservedChecker = (field, value) -> {
			return observedValues.computeIfAbsent(field.getName(), k -> new HashSet<>()).add(value);
		};
		return obj -> {
			boolean hasNewValue = false;
			for (Field field : obj.getClass().getFields()) {
				try {
					hasNewValue |= alreadyObservedChecker.apply(field, field.get(obj));
				} catch (IllegalArgumentException | IllegalAccessException cause) {
					throw new RuntimeException(cause);
				}
			}
			return hasNewValue;
		};
	}
}
