package fr.sazaju.genshin.service.controller.coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import fr.sazaju.genshin.service.controller.coder.Definition.DataReader;
import fr.sazaju.genshin.service.controller.coder.Definition.Input;

public class SequentialCoder<D> implements Coder<D, byte[]> {
	private final int bytes_count;
	private final List<PropertyCoder<D, ?>> propertyCodersSequence;
	private final DataReader<Input<D>, D> reader;
	private final List<Option> options;

	// TODO Create another which group booleans to optimize bytes
	public SequentialCoder(Definition<D> definition, Option... options) {
		this.propertyCodersSequence = definition.properties.stream()//
				.distinct()// No need to encode a value twice
				.map(PropertyCoder::wrap)//
				.collect(Collectors.toList());
		this.bytes_count = propertyCodersSequence.stream()//
				.mapToInt(PropertyCoder::getBytesSize)//
				.sum();
		this.reader = definition.reader;
		this.options = List.of(options);
	}

	@Override
	public byte[] encode(D data) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes_count);
		OutputStream out = baos;
		for (Option option : options) {
			out = option.outputStreamDecorator.decorate(out);
		}
		try (DataOutputStream dos = new DataOutputStream(out)) {
			for (PropertyCoder<D, ?> propertyCoder : propertyCodersSequence) {
				propertyCoder.writeValueFromData(data, dos);
			}
		}
		return baos.toByteArray();
	}

	@Override
	public D decode(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		InputStream in = bais;
		for (Option option : options) {
			in = option.inputStreamDecorator.decorate(in);
		}
		try (DataInputStream dis = new DataInputStream(in)) {
			// Deserialize all the data into a map
			Map<Property<D, ?>, Object> values = new HashMap<Property<D, ?>, Object>();
			for (PropertyCoder<D, ?> propertyCoder : propertyCodersSequence) {
				values.put(propertyCoder.property, propertyCoder.readValue(dis));
			}
			
			// Create an input which can randomly access each value
			Input<D> input = new Input<>() {

				@Override
				@SuppressWarnings("unchecked")
				public <T> T readValue(Property<D, T> property) {
					return (T) values.get(property);
				}

			};

			return reader.readData(input);
		}
	}

	public enum Option {
		GZIP(GZIPOutputStream::new, GZIPInputStream::new);

		private final Decorator<OutputStream> outputStreamDecorator;
		private final Decorator<InputStream> inputStreamDecorator;

		private Option(Decorator<OutputStream> outputStreamDecorator, Decorator<InputStream> inputStreamDecorator) {
			this.outputStreamDecorator = outputStreamDecorator;
			this.inputStreamDecorator = inputStreamDecorator;
		}
	}

	private interface Decorator<T> {
		T decorate(T source) throws IOException;
	}

	private static abstract class PropertyCoder<D, T> {

		public final Property<D, T> property;

		public PropertyCoder(Property<D, T> property) {
			this.property = property;
		}

		public void writeValueFromData(D data, DataOutputStream dos) throws IOException {
			writeValue(property.valueReader.readValue(data), dos);
		}

		protected abstract int getBytesSize();

		protected abstract void writeValue(T value, DataOutputStream dos) throws IOException;

		protected abstract T readValue(DataInputStream dis) throws IOException;

		@SuppressWarnings("unchecked")
		private static <D, T> PropertyCoder<D, T> wrap(Property<D, T> property) {
			Class<T> valueClass = property.valueClass;
			if (valueClass.equals(Double.class)) {
				return (PropertyCoder<D, T>) forDouble((Property<D, Double>) property);
			} else if (valueClass.equals(Integer.class)) {
				return (PropertyCoder<D, T>) forInt((Property<D, Integer>) property);
			} else if (valueClass.equals(Boolean.class)) {
				return (PropertyCoder<D, T>) forBool((Property<D, Boolean>) property);
			} else {
				throw new IllegalArgumentException(valueClass + " property not managed");
			}
		}

		private static <D> PropertyCoder<D, Boolean> forBool(Property<D, Boolean> property) {
			return new PropertyCoder<D, Boolean>(property) {

				@Override
				protected int getBytesSize() {
					return 1;
				}

				@Override
				protected void writeValue(Boolean value, DataOutputStream dos) throws IOException {
					dos.writeByte(value ? 1 : 0);
				}

				@Override
				protected Boolean readValue(DataInputStream dis) throws IOException {
					return dis.readByte() == 1;
				}
			};
		}

		private static <D> PropertyCoder<D, Integer> forInt(Property<D, Integer> property) {
			return new PropertyCoder<D, Integer>(property) {

				@Override
				protected int getBytesSize() {
					return Integer.BYTES;
				}

				@Override
				protected void writeValue(Integer value, DataOutputStream dos) throws IOException {
					dos.writeInt(value);
				}

				@Override
				protected Integer readValue(DataInputStream dis) throws IOException {
					return dis.readInt();
				}
			};
		}

		private static <D> PropertyCoder<D, Double> forDouble(Property<D, Double> property) {
			return new PropertyCoder<D, Double>(property) {

				@Override
				protected int getBytesSize() {
					return Double.BYTES;
				}

				@Override
				protected void writeValue(Double value, DataOutputStream dos) throws IOException {
					dos.writeDouble(value);
				}

				@Override
				protected Double readValue(DataInputStream dis) throws IOException {
					return dis.readDouble();
				}
			};
		}
	}

}
