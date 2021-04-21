package fr.sazaju.genshin.service.controller.coder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Property<D, T> {
	
	Class<T> getValueClass();
	
	ValueReader<D, T> getValueReader();

	@Deprecated// TODO Delegate to BytesCoder
	int getBytesSize();

	@Deprecated// TODO Delegate to BytesCoder
	T readValue(DataInputStream input) throws IOException;

	@Deprecated// TODO Delegate to BytesCoder
	void writeValue(D data, DataOutputStream output) throws IOException;

	public static <D> Property<D, Double> forDouble(ValueReader<D, Double> valueReader) {
		return new Property<>() {
			
			@Override
			public Class<Double> getValueClass() {
				return Double.class;
			}
			
			@Override
			public ValueReader<D, Double> getValueReader() {
				return valueReader;
			}

			@Override
			public int getBytesSize() {
				return Double.BYTES;
			}

			@Override
			public void writeValue(D data, DataOutputStream output) throws IOException {
				output.writeDouble(valueReader.read(data));
			}

			@Override
			public Double readValue(DataInputStream input) throws IOException {
				return input.readDouble();
			}
		};
	}

	public static <D> Property<D, Integer> forInt(ValueReader<D, Integer> valueReader) {
		return new Property<>() {
			
			@Override
			public Class<Integer> getValueClass() {
				return Integer.class;
			}
			
			@Override
			public ValueReader<D, Integer> getValueReader() {
				return valueReader;
			}

			@Override
			public int getBytesSize() {
				return Integer.BYTES;
			}

			@Override
			public void writeValue(D data, DataOutputStream output) throws IOException {
				output.writeInt(valueReader.read(data));
			}

			@Override
			public Integer readValue(DataInputStream input) throws IOException {
				return input.readInt();
			}
		};
	}

}

interface ValueReader<D, T> {
	T read(D t);
}