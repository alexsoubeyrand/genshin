package fr.sazaju.genshin.service.controller.coder;

public class Property<D, T> {

	public final Class<T> valueClass;
	public final ValueReader<D, T> valueReader;

	interface ValueReader<D, T> {
		T readValue(D data);
	}

	public Property(Class<T> valueClass, ValueReader<D, T> valueReader) {
		this.valueClass = valueClass;
		this.valueReader = valueReader;
	}

}
