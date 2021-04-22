package fr.sazaju.genshin.service.controller.coder;

import java.io.IOException;
import java.util.Collection;

public class Definition<D> {
	public interface Input<D> {

		<T> T readValue(Property<D, T> property);
	}

	public interface DataReader<I, D> {
		D readData(I input) throws IOException;
	}

	public final Collection<Property<D, ?>> properties;
	public final DataReader<Input<D>, D> reader;

	public Definition(Collection<Property<D, ?>> properties, DataReader<Input<D>, D> reader) {
		this.properties = properties;
		this.reader = reader;
	}
}
