package fr.sazaju.genshin.service.controller.coder;

public class Property<D, T> {

	public final ValueReader<D, T> valueReader;

	interface ValueReader<D, T> {
		T readValue(D data);
	}

	private Property(ValueReader<D, T> valueReader) {
		this.valueReader = valueReader;
	}

	public static <D, T> ClassProperty<D, T> onClass(Class<? extends T> valueClass, ValueReader<D, T> valueReader) {
		return new ClassProperty<>(valueClass, valueReader);
	}

	public static <D, T> ListProperty<D, T> onList(Class<T> itemClass, ValueReader<D, java.util.List<T>> valueReader) {
		return new ListProperty<D, T>(itemClass, valueReader);
	}

	public static <D, T> DefinitionProperty<D, T> onDefinition(Definition<T> definition,
			ValueReader<D, T> valueReader) {
		return new DefinitionProperty<D, T>(definition, valueReader);
	}

	public static class ClassProperty<D, T> extends Property<D, T> {

		public final Class<? extends T> valueClass;

		private ClassProperty(Class<? extends T> valueClass, ValueReader<D, T> valueReader) {
			super(valueReader);
			this.valueClass = valueClass;
		}

	}

	public static class ListProperty<D, T> extends Property<D, java.util.List<T>> {

		public final Class<T> itemClass;

		private ListProperty(Class<T> itemClass, ValueReader<D, java.util.List<T>> valueReader) {
			super(valueReader);
			this.itemClass = itemClass;
		}

	}

	public static class DefinitionProperty<D, T> extends Property<D, T> {

		public final Definition<T> definition;

		private DefinitionProperty(Definition<T> definition, ValueReader<D, T> valueReader) {
			super(valueReader);
			this.definition = definition;
		}

	}
}
