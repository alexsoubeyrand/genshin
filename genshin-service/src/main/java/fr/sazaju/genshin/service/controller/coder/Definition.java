package fr.sazaju.genshin.service.controller.coder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class Definition<D> {
	public interface Input<D> {

		<T> T readValue(Property<D, T> property);
	}

	public interface DataWriter<O, D> {
		void writeData(D data, O output) throws IOException;
	}

	public interface DataReader<I, D> {
		D readData(I input) throws IOException;
	}

	public final int bytes_count;
	public final DataWriter<DataOutputStream, D> dataWriter;
	public final DataReader<DataInputStream, D> dataReader;

	public Definition(int bytes_count, DataWriter<DataOutputStream, D> dataWriter, DataReader<DataInputStream, D> dataReader) {
		this.bytes_count = bytes_count;
		this.dataWriter = dataWriter;
		this.dataReader = dataReader;
	}

	// TODO Create optimized definition which groups booleans
	public static <D> Definition<D> createSequentialDefinition(Collection<Property<D, ?>> properties,
			DataReader<Input<D>, D> reader) {
		ArrayList<Property<D, ?>> orderedProperties = new ArrayList<>(new LinkedHashSet<>(properties));
		int bytes_count = properties.stream().mapToInt(Property::getBytesSize).sum();
		DataWriter<DataOutputStream, D> dataWriter = (data, output) -> {
			for (Property<D, ?> property : orderedProperties) {
				property.writeValue(data, output);
			}
		};
		DataReader<DataInputStream, D> dataReader = new DataReader<>() {

			@Override
			public D readData(DataInputStream input) throws IOException {
				Map<Property<D, ?>, Object> values = new HashMap<Property<D, ?>, Object>();
				for (Property<D, ?> property : orderedProperties) {
					Object value = property.readValue(input);
					values.put(property, value);
				}
				Input<D> input2 = new Input<>() {

					@SuppressWarnings("unchecked")
					@Override
					public <T> T readValue(Property<D, T> property) {
						return (T) values.get(property);
					}

				};

				return reader.readData(input2);
			}
		};
		return new Definition<D>(bytes_count, dataWriter, dataReader);
	}
}
