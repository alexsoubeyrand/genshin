package fr.sazaju.genshin.service;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

public class Assertions {
	public static <T> void assertPublicFieldsAreEqual(T expected, T actual) {
		Class<?> clazz = expected.getClass();
		Field[] fields = clazz.getFields();
		for (Field field : fields) {
			try {
				Object expectedValue = field.get(expected);
				Object actualValue = field.get(actual);
				assertEquals(expectedValue, actualValue,
						() -> "Field " + clazz.getName() + "." + field.getName() + " is different");
			} catch (IllegalArgumentException | IllegalAccessException cause) {
				throw new RuntimeException(cause);
			}
		}
	}
}
