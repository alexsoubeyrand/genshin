package fr.sazaju.genshin.service.controller.coder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface Definition<D> {

	public static <D> Builder<D> onClass(Class<D> clazz) {
		return new Builder<D>();
	}

	// Must be used with explicit generic
	public static <D> Builder<D> onGenericClass() {
		return new Builder<D>();
	}

	public static <D> FixedDefinition<D> onProperties(List<Property<? super D, ?>> properties, DataReader<D> reader) {
		return Definition.<D>onGenericClass().withProperties(properties, reader);
	}

	public static class Builder<D> {

		public FixedDefinition<D> withProperties(List<Property<? super D, ?>> properties, DataReader<D> reader) {
			return new FixedDefinition<D>(properties, reader);
		}

		public DynamicBuilder<D> when(Predicate<D> predicate, Definition<? extends D> definition) {
			return new DynamicBuilder<D>().elseWhen(predicate, definition);
		}

		public <T extends D> DynamicBuilder<D> when(Class<T> subType, Definition<T> definition) {
			return new DynamicBuilder<D>().elseWhen(subType, definition);
		}
	}

	static class DynamicBuilder<D> {

		private final LinkedHashMap<Predicate<D>, Definition<? extends D>> conditionalDefinitions = new LinkedHashMap<>();
		private final List<Definition<? extends D>> allDefinitions = new LinkedList<>();

		public DynamicBuilder<D> elseWhen(Predicate<D> predicate, Definition<? extends D> definition) {
			conditionalDefinitions.put(predicate, definition);
			allDefinitions.add(definition);
			return this;
		}

		public <T extends D> DynamicBuilder<D> elseWhen(Class<T> subType, Definition<T> definition) {
			return elseWhen(subType::isInstance, definition);
		}

		public DynamicDefinition<D> elseUse(Definition<? extends D> defaultDefinition) {
			allDefinitions.add(defaultDefinition);
			DefinitionSelector<D> definitionSelector = data -> {
				return findCorrespondingDefinition(data).orElse(defaultDefinition);
			};
			return new DynamicDefinition<D>(definitionSelector, allDefinitions);
		}

		public DynamicDefinition<D> elseThrow(Function<D, RuntimeException> exceptionSupplier) {
			DefinitionSelector<D> definitionSelector = data -> {
				return findCorrespondingDefinition(data).orElseThrow(() -> exceptionSupplier.apply(data));
			};
			return new DynamicDefinition<D>(definitionSelector, allDefinitions);
		}

		private Optional<Definition<? extends D>> findCorrespondingDefinition(D data) {
			return conditionalDefinitions.entrySet().stream()//
					.filter(definitionCase -> definitionCase.getKey().test(data))//
					.<Definition<? extends D>>map(definitionCase -> definitionCase.getValue())//
					.findFirst();
		}

	}

	public interface Input<D> {

		<T> T readValue(Property<? super D, T> property);
	}

	public interface DataReader<D> {
		D readData(Input<D> input) throws IOException;
	}

	interface DefinitionSelector<D> {
		Definition<? extends D> selectDefinition(D data);
	}

	public static class FixedDefinition<D> implements Definition<D> {

		// Although it is a List it is also guaranteed to have no multiple occurrences.
		public final List<Property<? super D, ?>> properties;
		public final DataReader<D> reader;

		private FixedDefinition(List<Property<? super D, ?>> properties, DataReader<D> reader) {
			// Consume List to keep the same order between runs.
			// We make it a "set" by ourselves by removing the multiple occurrences.
			this.properties = properties.stream().distinct().collect(Collectors.toList());
			this.reader = reader;
		}

	}

	public static class DynamicDefinition<D> implements Definition<D> {

		public final DefinitionSelector<D> selector;
		public final List<Definition<? extends D>> allDefinitions;

		private DynamicDefinition(DefinitionSelector<D> selector, List<Definition<? extends D>> allDefinitions) {
			this.selector = selector;
			this.allDefinitions = Collections.unmodifiableList(new ArrayList<>(allDefinitions));
		}

	}
}
