package fr.sazaju.genshin.service.controller.coder;

import static fr.sazaju.genshin.simulator.NumberGenerator.*;

import java.util.List;

import fr.sazaju.genshin.StringUtils;
import fr.sazaju.genshin.simulator.NumberGenerator;
import fr.sazaju.genshin.simulator.NumberGenerator.SplittableNumberGenerator;

public class NumberGeneratorDescriptorDefinition {

	public static abstract class NumberGeneratorDescriptor<G extends NumberGenerator> {
		public final int wishesCount;

		public NumberGeneratorDescriptor(int wishesCount) {
			this.wishesCount = wishesCount;
		}

		public abstract G createNumberGenerator();

		public abstract NumberGeneratorDescriptor<G> prepareNextDescriptor(G generator, int wishesCount);

		@Override
		public String toString() {
			return StringUtils.toStringFromFields(this);
		}
	}

	public static class FixedNGDescriptor extends NumberGeneratorDescriptor<NumberGenerator> {
		public final float fixedValue;

		public FixedNGDescriptor(float value, int wishesCount) {
			super(wishesCount);
			this.fixedValue = value;
		}

		@Override
		public NumberGenerator createNumberGenerator() {
			return createFixedNumberGenerator(fixedValue);
		}

		@Override
		public NumberGeneratorDescriptor<NumberGenerator> prepareNextDescriptor(NumberGenerator generator,
				int wishesCount) {
			return new FixedNGDescriptor(fixedValue, wishesCount);
		}
	}

	public static class ListNGDescriptor extends NumberGeneratorDescriptor<SplittableNumberGenerator<Integer>> {
		public final List<Float> values;
		public final int offset;

		public ListNGDescriptor(List<Float> values, int offset, int wishesCount) {
			super(wishesCount);
			this.values = values;
			this.offset = offset;
		}

		@Override
		public SplittableNumberGenerator<Integer> createNumberGenerator() {
			return createListNumberGenerator(values, offset);
		}

		@Override
		public NumberGeneratorDescriptor<SplittableNumberGenerator<Integer>> prepareNextDescriptor(
				SplittableNumberGenerator<Integer> generator, int wishesCount) {
			return new ListNGDescriptor(values, generator.getNextSeed(), wishesCount);
		}
	}

	public static class RandomNGDescriptor extends NumberGeneratorDescriptor<SplittableNumberGenerator<Long>> {

		public final long seed;

		public RandomNGDescriptor(long seed, int wishesCount) {
			super(wishesCount);
			this.seed = seed;
		}

		@Override
		public SplittableNumberGenerator<Long> createNumberGenerator() {
			return createRandomNumberGenerator(seed);
		}

		@Override
		public NumberGeneratorDescriptor<SplittableNumberGenerator<Long>> prepareNextDescriptor(
				SplittableNumberGenerator<Long> generator, int wishesCount) {
			return new RandomNGDescriptor(generator.getNextSeed(), wishesCount);
		}

	}

	private static final Property<NumberGeneratorDescriptor<?>, Integer> wishesCount = //
			Property.onClass(Integer.class, settings -> settings.wishesCount);
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
							List.of(wishesCount, fixedValue), //
							(input) -> new FixedNGDescriptor(//
									input.readValue(fixedValue), //
									input.readValue(wishesCount)//
							)//
					))//
					.elseWhen(ListNGDescriptor.class, Definition.onProperties(//
							List.of(wishesCount, offset, values), //
							(input) -> new ListNGDescriptor(//
									input.readValue(values), //
									input.readValue(offset), //
									input.readValue(wishesCount)//
							)//
					))//
					.elseWhen(RandomNGDescriptor.class, Definition.onProperties(//
							List.of(wishesCount, seed), //
							(input) -> new RandomNGDescriptor(//
									input.readValue(seed), //
									input.readValue(wishesCount)//
							)//
					))//
					.elseThrow(descriptor -> new RuntimeException("Not managed case: " + descriptor.getClass()));
}
