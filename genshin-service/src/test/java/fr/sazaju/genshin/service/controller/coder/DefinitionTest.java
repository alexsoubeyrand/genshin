package fr.sazaju.genshin.service.controller.coder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.sazaju.genshin.service.controller.coder.Definition.FixedDefinition;

class DefinitionTest {

	@Test
	void testDefinitionOnPropertiesStoresPropertiesOnce() {
		Property<Object, Integer> property = Property.onClass(Integer.class, obj -> 123);
		FixedDefinition<Object> definition = Definition.onProperties(//
				List.of(property, property), //
				input -> new Object()//
		);
		assertEquals(List.of(property), definition.properties);
	}

}
