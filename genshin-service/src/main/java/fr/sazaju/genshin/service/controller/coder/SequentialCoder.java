package fr.sazaju.genshin.service.controller.coder;

import static java.util.stream.Collectors.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import fr.sazaju.genshin.service.controller.coder.Definition.DataReader;
import fr.sazaju.genshin.service.controller.coder.Definition.DefinitionSelector;
import fr.sazaju.genshin.service.controller.coder.Definition.DynamicDefinition;
import fr.sazaju.genshin.service.controller.coder.Definition.FixedDefinition;
import fr.sazaju.genshin.service.controller.coder.Definition.Input;
import fr.sazaju.genshin.service.controller.coder.Property.ClassProperty;
import fr.sazaju.genshin.service.controller.coder.Property.DefinitionProperty;
import fr.sazaju.genshin.service.controller.coder.Property.ListProperty;

// TODO Create a coder which groups booleans, including for List<Boolean>
public class SequentialCoder<D> implements Coder<D, byte[]> {
	private final List<Option> options;
	private final PropertyCoder<D, D> coder;

	public SequentialCoder(Definition<D> definition, Option... options) {
		this.coder = PropertyCoder.fromDefinitionProperty(Property.onDefinition(definition, data -> data));
		this.options = List.of(options);
	}

	@Override
	public byte[] encode(D data) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(coder.getBytesSize(data));
		OutputStream out = baos;
		for (Option option : options) {
			out = option.outputStreamDecorator.decorate(out);
		}
		try (DataOutputStream dos = new DataOutputStream(out)) {
			coder.writeValue(data, dos);
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
			return coder.readValue(dis);
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

		private final Property<D, T> property;

		public PropertyCoder(Property<D, T> property) {
			this.property = property;
		}

		protected abstract int getBytesSize(D data);

		public void writeValueFromData(D data, DataOutputStream dos) throws IOException {
			writeValue(property.valueReader.readValue(data), dos);
		}

		protected abstract void writeValue(T value, DataOutputStream dos) throws IOException;

		protected abstract T readValue(DataInputStream dis) throws IOException;

		@SuppressWarnings("unchecked")
		private static <D, T> PropertyCoder<D, T> fromProperty(Property<D, T> property) {
			if (property instanceof DefinitionProperty) {
				return fromDefinitionProperty((DefinitionProperty<D, T>) property);
			} else if (property instanceof ListProperty) {
				return (PropertyCoder<D, T>) fromListProperty((ListProperty<D, ?>) property);
			} else if (property instanceof ClassProperty) {
				Class<? extends T> valueClass = ((ClassProperty<D, T>) property).valueClass;
				if (valueClass.equals(Double.class)) {
					return (PropertyCoder<D, T>) fromDoubleProperty((Property<D, Double>) property);
				} else if (valueClass.equals(Float.class)) {
					return (PropertyCoder<D, T>) fromFloatProperty((Property<D, Float>) property);
				} else if (valueClass.equals(Integer.class)) {
					return (PropertyCoder<D, T>) fromIntProperty((Property<D, Integer>) property);
				} else if (valueClass.equals(Long.class)) {
					return (PropertyCoder<D, T>) fromLongProperty((Property<D, Long>) property);
				} else if (valueClass.equals(Boolean.class)) {
					return (PropertyCoder<D, T>) fromBoolProperty((Property<D, Boolean>) property);
				} else {
					throw new IllegalArgumentException("Class not managed: " + valueClass);
				}
			} else {
				throw new IllegalArgumentException("Property not managed: " + property);
			}
		}

		private static <D, T> PropertyCoder<D, T> fromDefinitionProperty(DefinitionProperty<D, T> property) {
			Definition<T> definition = property.definition;
			if (definition instanceof DynamicDefinition) {
				return fromDynamicDefinitionProperty(property, (DynamicDefinition<T>) definition);
			} else if (definition instanceof FixedDefinition) {
				return fromFixedDefinitionProperty(property, (FixedDefinition<T>) definition);
			} else {
				throw new RuntimeException("Definition not managed: " + definition);
			}
		}

		private static <D> PropertyCoder<D, Boolean> fromBoolProperty(Property<D, Boolean> property) {
			return new PropertyCoder<D, Boolean>(property) {

				@Override
				protected int getBytesSize(D data) {
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

		private static <D> PropertyCoder<D, Long> fromLongProperty(Property<D, Long> property) {
			return new PropertyCoder<D, Long>(property) {

				@Override
				protected int getBytesSize(D data) {
					return Integer.BYTES;
				}

				@Override
				protected void writeValue(Long value, DataOutputStream dos) throws IOException {
					dos.writeLong(value);
				}

				@Override
				protected Long readValue(DataInputStream dis) throws IOException {
					return dis.readLong();
				}
			};
		}

		private static <D> PropertyCoder<D, Integer> fromIntProperty(Property<D, Integer> property) {
			return new PropertyCoder<D, Integer>(property) {

				@Override
				protected int getBytesSize(D data) {
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

		private static <D> PropertyCoder<D, Float> fromFloatProperty(Property<D, Float> property) {
			return new PropertyCoder<D, Float>(property) {

				@Override
				protected int getBytesSize(D data) {
					return Double.BYTES;
				}

				@Override
				protected void writeValue(Float value, DataOutputStream dos) throws IOException {
					dos.writeFloat(value);
				}

				@Override
				protected Float readValue(DataInputStream dis) throws IOException {
					return dis.readFloat();
				}
			};
		}

		private static <D> PropertyCoder<D, Double> fromDoubleProperty(Property<D, Double> property) {
			return new PropertyCoder<D, Double>(property) {

				@Override
				protected int getBytesSize(D data) {
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

		private static <D, T> PropertyCoder<D, List<T>> fromListProperty(ListProperty<D, T> property) {
			PropertyCoder<T, T> itemCoder = PropertyCoder
					.fromProperty(Property.onClass(property.itemClass, data -> data));
			return new PropertyCoder<D, List<T>>(property) {

				@Override
				protected int getBytesSize(D data) {
					return property.valueReader.readValue(data).stream()//
							.mapToInt(item -> itemCoder.getBytesSize(item))//
							.sum();
				}

				@Override
				protected void writeValue(List<T> items, DataOutputStream dos) throws IOException {
					dos.writeInt(items.size());
					for (T item : items) {
						itemCoder.writeValueFromData(item, dos);
					}
				}

				@Override
				protected List<T> readValue(DataInputStream dis) throws IOException {
					List<T> list = new LinkedList<>();
					int size = dis.readInt();
					for (int i = 0; i < size; i++) {
						list.add(itemCoder.readValue(dis));
					}
					return list;
				}
			};
		}

		private static <T, D> PropertyCoder<D, T> fromFixedDefinitionProperty(DefinitionProperty<D, T> property,
				FixedDefinition<T> definition) {
			List<PropertyCoder<? super T, ?>> subPropertyCodersSequence = definition.properties.stream()//
					.map(subProperty -> PropertyCoder.fromProperty(subProperty))//
					.collect(toList());
			DataReader<T> reader = definition.reader;

			return new PropertyCoder<D, T>(property) {

				@Override
				protected int getBytesSize(D data) {
					T value = property.valueReader.readValue(data);
					return subPropertyCodersSequence.stream()//
							.mapToInt(subProperty -> subProperty.getBytesSize(value))//
							.sum();
				}

				@Override
				protected void writeValue(T value, DataOutputStream dos) throws IOException {
					for (PropertyCoder<? super T, ?> subPropertyCoder : subPropertyCodersSequence) {
						subPropertyCoder.writeValueFromData(value, dos);
					}
				}

				@Override
				protected T readValue(DataInputStream dis) throws IOException {
					// Deserialize all the data into a map
					Map<Property<? super T, ?>, Object> values = new HashMap<>();
					for (PropertyCoder<? super T, ?> subPropertyCoder : subPropertyCodersSequence) {
						values.put(subPropertyCoder.property, subPropertyCoder.readValue(dis));
					}

					// Create an input which can randomly access each value
					Input<T> input = new Input<>() {

						@Override
						@SuppressWarnings("unchecked")
						public <U> U readValue(Property<? super T, U> property) {
							return (U) values.get(property);
						}

					};

					return reader.readData(input);
				}
			};
		}

		private static <T, D> PropertyCoder<D, T> fromDynamicDefinitionProperty(DefinitionProperty<D, T> property,
				DynamicDefinition<T> definition) {
			Map<Definition<? extends T>, Integer> ids = assignIds(definition);
			Map<Integer, PropertyCoder<T, ? extends T>> subCoders = assignCoders(ids);
			DefinitionSelector<T> subDefinitionSelector = definition.selector;
			return new PropertyCoder<D, T>(property) {

				private int getId(T data) {
					return ids.get(subDefinitionSelector.selectDefinition(data));
				}

				@Override
				protected int getBytesSize(D data) {
					T subData = property.valueReader.readValue(data);
					return subCoders.get(getId(subData)).getBytesSize(subData);
				}

				@Override
				protected void writeValue(T data, DataOutputStream dos) throws IOException {
					int id = getId(data);
					dos.writeInt(id);
					subCoders.get(id).writeValueFromData(data, dos);
				}

				@Override
				protected T readValue(DataInputStream dis) throws IOException {
					int id = dis.readInt();
					return subCoders.get(id).readValue(dis);
				}
			};
		}

		private static <T> Map<Integer, PropertyCoder<T, ? extends T>> assignCoders(
				Map<Definition<? extends T>, Integer> ids) {
			return ids.entrySet().stream()//
					.map(entry -> {
						Integer id = entry.getValue();
						Definition<? extends T> subDefinition = entry.getKey();
						Property<T, ? extends T> subProperty = propertyOnSubtype(subDefinition);
						PropertyCoder<T, ? extends T> subCoder = PropertyCoder.fromProperty(subProperty);
						return Map.entry(id, subCoder);
					})//
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		}

		@SuppressWarnings("unchecked") // Used specifically on sub-type, so we know it is correct
		private static <D, T extends D> Property<D, T> propertyOnSubtype(Definition<T> definition) {
			return Property.onDefinition(definition, data -> (T) data);
		}

		private static <T> Map<Definition<? extends T>, Integer> assignIds(DynamicDefinition<T> compositeDefinition) {
			int[] nextId = { 0 };
			return compositeDefinition.allDefinitions.stream()//
					.collect(toMap(//
							coder -> coder, //
							coder -> nextId[0]++//
					));
		}
	}

}
