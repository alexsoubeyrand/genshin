package fr.sazaju.genshin.service.controller.coder;

import static fr.sazaju.genshin.banner.NumberGenerator.*;

import java.util.List;

import fr.sazaju.genshin.StringUtils;
import fr.sazaju.genshin.banner.NumberGenerator;
import fr.sazaju.genshin.banner.NumberGenerator.SplittableNumberGenerator;

public class NumberGeneratorDescriptorDefinition {

	public static interface NumberGeneratorDescriptor<G extends NumberGenerator> {
		public abstract G createNumberGenerator();

		public abstract NumberGeneratorDescriptor<G> prepareNextDescriptor(G generator);
	}

	public static class FixedNGDescriptor implements NumberGeneratorDescriptor<NumberGenerator> {
		public final float fixedValue;

		public FixedNGDescriptor(float value) {
			this.fixedValue = value;
		}

		@Override
		public NumberGenerator createNumberGenerator() {
			return createFixedNumberGenerator(fixedValue);
		}

		@Override
		public NumberGeneratorDescriptor<NumberGenerator> prepareNextDescriptor(NumberGenerator generator) {
			return new FixedNGDescriptor(fixedValue);
		}

		@Override
		public String toString() {
			return StringUtils.toStringFromFields(this);
		}
	}

	public static class ListNGDescriptor implements NumberGeneratorDescriptor<SplittableNumberGenerator<Integer>> {
		public final List<Float> values;
		public final int offset;

		public ListNGDescriptor(List<Float> values, int offset) {
			this.values = values;
			this.offset = offset;
		}

		@Override
		public SplittableNumberGenerator<Integer> createNumberGenerator() {
			return createListNumberGenerator(values, offset);
		}

		@Override
		public NumberGeneratorDescriptor<SplittableNumberGenerator<Integer>> prepareNextDescriptor(
				SplittableNumberGenerator<Integer> generator) {
			return new ListNGDescriptor(values, generator.getNextSeed());
		}

		@Override
		public String toString() {
			return StringUtils.toStringFromFields(this);
		}
	}

	public static class RandomNGDescriptor implements NumberGeneratorDescriptor<SplittableNumberGenerator<Long>> {

		public final long seed;

		public RandomNGDescriptor(long seed) {
			this.seed = seed;
		}

		@Override
		public SplittableNumberGenerator<Long> createNumberGenerator() {
			return createRandomNumberGenerator(seed);
		}

		@Override
		public NumberGeneratorDescriptor<SplittableNumberGenerator<Long>> prepareNextDescriptor(
				SplittableNumberGenerator<Long> generator) {
			return new RandomNGDescriptor(generator.getNextSeed());
		}

		@Override
		public String toString() {
			return StringUtils.toStringFromFields(this);
		}
	}

	private static final Property<FixedNGDescriptor, Float> fixedValue = //
			Property.onClass(Float.class, settings -> settings.fixedValue);
	private static final Property<ListNGDescriptor, List<Float>> values = //
			Property.onList(Float.class, settings -> settings.values);
	private static final Property<ListNGDescriptor, Integer> offset = //
			Property.onClass(Integer.class, settings -> settings.offset);
	private static final Property<RandomNGDescriptor, Long> seed = //
			Property.onClass(Long.class, settings -> settings.seed);

	public static final Definition<NumberGeneratorDescriptor<?>> V1 = //
			Definition.<NumberGeneratorDescriptor<?>>onGenericClass()//
					.when(FixedNGDescriptor.class, Definition.onProperties(//
							List.of(fixedValue), //
							(input) -> new FixedNGDescriptor(//
									input.readValue(fixedValue) //
							)//
					))//
					.elseWhen(ListNGDescriptor.class, Definition.onProperties(//
							List.of(offset, values), //
							(input) -> new ListNGDescriptor(//
									input.readValue(values), //
									input.readValue(offset) //
							)//
					))//
					.elseWhen(RandomNGDescriptor.class, Definition.onProperties(//
							List.of(seed), //
							(input) -> new RandomNGDescriptor(//
									input.readValue(seed) //
							)//
					))//
					.elseThrow(descriptor -> new RuntimeException("Not managed case: " + descriptor.getClass()));
}
