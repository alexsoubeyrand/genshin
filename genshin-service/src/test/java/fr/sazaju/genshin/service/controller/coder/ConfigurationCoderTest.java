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

import fr.sazaju.genshin.banner.character.Settings;
import fr.sazaju.genshin.banner.character.State;
import fr.sazaju.genshin.service.controller.coder.ConfigurationDefinition.Configuration;
import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.FixedNGDescriptor;
import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.ListNGDescriptor;
import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.NumberGeneratorDescriptor;
import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.RandomNGDescriptor;

class ConfigurationCoderTest implements CollectionCoderTest<Configuration> {

	@ParameterizedTest
	@MethodSource("allCodersAndConfigurationsData")
	public void testEachVersionHasConsistentCoding(//
			ConfigurationCoder coder, //
			Settings settings, //
			State state, //
			NumberGeneratorDescriptor<?> numberGeneratorDescriptor) throws IOException {

		Configuration source = new Configuration(settings, state, numberGeneratorDescriptor);
		Configuration rebuilt = coder.decode(coder.encode(source));
		assertPublicFieldsAreEqual(settings, rebuilt.settings);
		assertPublicFieldsAreEqual(state, rebuilt.state);
		assertPublicFieldsAreEqual(numberGeneratorDescriptor, rebuilt.numberGeneratorDescriptor);
	}

	@Override
	public Coder<Configuration, String> searchSerialCoder(String serial) {
		return ConfigurationCoder.fromSerial(serial);
	}

	@Override
	public Stream<Coder<Configuration, String>> allCoders() {
		return Stream.of(ConfigurationCoder.values());
	}

	public Stream<Settings> someSettingsValues() {
		return new SettingsCoderTest().allData().filter(onItemsProvidingNewFieldValues());
	}

	public Stream<State> someStateValues() {
		return new StateCoderTest().allData().filter(onItemsProvidingNewFieldValues());
	}

	public Stream<NumberGeneratorDescriptor<?>> someNumberGeneratorDescriptorValues() {
		// TODO Complete?
		return Stream.of(//
				new FixedNGDescriptor(0.5f), //
				new ListNGDescriptor(List.of(0.0f, 0.5f, 1.0f), 0), //
				new RandomNGDescriptor(12345L));
	}

	@Override
	public Stream<Configuration> allData() {
		return someSettingsValues().flatMap(settings -> //
		someStateValues().flatMap(state -> //
		someNumberGeneratorDescriptorValues().flatMap(numberGeneratorDescriptor -> //
		Stream.of(new Configuration(settings, state, numberGeneratorDescriptor))//
		)));
	}

	public Stream<Arguments> allCodersAndConfigurationsData() {
		return allCoders().flatMap(coder -> //
		someSettingsValues().flatMap(settings -> //
		someStateValues().flatMap(state -> //
		someNumberGeneratorDescriptorValues().flatMap(numberGeneratorDescriptor -> //
		Stream.of(arguments(coder, settings, state, numberGeneratorDescriptor))//
		))));
	}
}
