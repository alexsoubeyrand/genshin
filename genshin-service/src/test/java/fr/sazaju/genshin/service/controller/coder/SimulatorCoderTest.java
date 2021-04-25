package fr.sazaju.genshin.service.controller.coder;

import static fr.sazaju.genshin.service.Assertions.*;
import static fr.sazaju.genshin.service.utils.FilterUtils.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.FixedNGDescriptor;
import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.ListNGDescriptor;
import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.NumberGeneratorDescriptor;
import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.RandomNGDescriptor;
import fr.sazaju.genshin.service.controller.coder.SimulatorDefinition.Simulator;
import fr.sazaju.genshin.simulator.wish.Profile;
import fr.sazaju.genshin.simulator.wish.Settings;

class SimulatorCoderTest implements CollectionCoderTest<Simulator> {

	@ParameterizedTest
	@MethodSource("allCodersAndSimulatorsData")
	public void testEachVersionHasConsistentCoding(//
			SimulatorCoder coder, //
			Settings settings, //
			Profile profile, //
			NumberGeneratorDescriptor<?> numberGeneratorDescriptor) throws IOException {

		Simulator source = new Simulator(settings, profile, numberGeneratorDescriptor);
		Simulator rebuilt = coder.decode(coder.encode(source));
		assertPublicFieldsAreEqual(settings, rebuilt.settings);
		assertPublicFieldsAreEqual(profile, rebuilt.profile);
		assertPublicFieldsAreEqual(numberGeneratorDescriptor, rebuilt.numberGeneratorDescriptor);
	}

	@Override
	public Coder<Simulator, String> searchSerialCoder(String serial) {
		return SimulatorCoder.fromSerial(serial);
	}

	@Override
	public Stream<Coder<Simulator, String>> allCoders() {
		return Stream.of(SimulatorCoder.values());
	}

	public Stream<Settings> someSettingsValues() {
		return new SettingsCoderTest().allData().filter(onItemsProvidingNewFieldValues());
	}

	public Stream<Profile> someProfileValues() {
		return new ProfileCoderTest().allData().filter(onItemsProvidingNewFieldValues());
	}

	public Stream<NumberGeneratorDescriptor<?>> someNumberGeneratorDescriptorValues() {
		// TODO Complete?
		return Stream.of(//
				new FixedNGDescriptor(0.5f, 3), //
				new ListNGDescriptor(List.of(0.0f, 0.5f, 1.0f), 0, 3), //
				new RandomNGDescriptor(12345L, 3));
	}

	@Override
	public Stream<Simulator> allData() {
		return someSettingsValues().flatMap(settings -> //
		someProfileValues().flatMap(profile -> //
		someNumberGeneratorDescriptorValues().flatMap(numberGeneratorDescriptor -> //
		Stream.of(new Simulator(settings, profile, numberGeneratorDescriptor))//
		)));
	}

	public Stream<Arguments> allCodersAndSimulatorsData() {
		return allCoders().flatMap(coder -> //
		someSettingsValues().flatMap(settings -> //
		someProfileValues().flatMap(profile -> //
		someNumberGeneratorDescriptorValues().flatMap(numberGeneratorDescriptor -> //
		Stream.of(arguments(coder, settings, profile, numberGeneratorDescriptor))//
		))));
	}
}
