package fr.sazaju.genshin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class StringUtils {
	public static String toStringFromFields(Object object) {
		return toStringFormFields(object, value -> value);
	}

	public static String toStringFromFieldsRecursive(Object object) {
		return toStringFormFields(object, value -> toStringFromFieldsRecursive(value));
	}

	public static String toStringFormFields(Object object, Function<Object, Object> valueAdapter) {
		Map<String, Object> values = new LinkedHashMap<>();
		Class<?> clazz = object.getClass();
		Field[] fields = clazz.getFields();
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue; // Ignore static fields
			}
			field.setAccessible(true);
			String name = field.getName();
			Object value;
			try {
				value = field.get(object);
			} catch (IllegalArgumentException | IllegalAccessException cause) {
				throw new RuntimeException(cause);
			}
			values.put(name, valueAdapter.apply(value));
		}
		return values.isEmpty() ? object.toString() : clazz.getSimpleName() + values;
	}
}
