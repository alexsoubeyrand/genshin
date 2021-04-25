package fr.sazaju.genshin;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class StringUtils {
	public static String toStringFromFields(Object object) {
		Map<String, Object> values = new LinkedHashMap<>();
		Class<?> clazz = object.getClass();
		Field[] fields = clazz.getFields();
		for (Field field : fields) {
			String name = field.getName();
			Object value;
			try {
				value = field.get(object);
			} catch (IllegalArgumentException | IllegalAccessException cause) {
				throw new RuntimeException(cause);
			}
			values.put(name, value);
		}
		return clazz.getSimpleName() + values.toString();
	}
}
