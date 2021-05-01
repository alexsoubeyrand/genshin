package fr.sazaju.genshin.service.controller.coder;

import java.util.List;

import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.NumberGeneratorDescriptor;
import fr.sazaju.genshin.simulator.wish.State;
import fr.sazaju.genshin.simulator.wish.Settings;

public class ConfigurationDefinition {

	public static class Configuration {
		public final Settings settings;
		public final State state;
		public final NumberGeneratorDescriptor<?> numberGeneratorDescriptor;

		public Configuration(Settings settings, State state, NumberGeneratorDescriptor<?> numberGeneratorDescriptor) {
			this.settings = settings;
			this.state = state;
			this.numberGeneratorDescriptor = numberGeneratorDescriptor;
		}
	}

	private static final Property<Configuration, Settings> settings = //
			Property.onDefinition(SettingsDefinition.V1, simulator -> simulator.settings);
	private static final Property<Configuration, State> state = //
			Property.onDefinition(StateDefinition.V1, simulator -> simulator.state);
	private static final Property<Configuration, NumberGeneratorDescriptor<?>> numberGeneratorDescriptor = //
			Property.onDefinition(NumberGeneratorDescriptorDefinition.V1,
					simulator -> simulator.numberGeneratorDescriptor);

	public static final Definition<Configuration> V1 = Definition.onProperties(//
			List.of(//
					settings, //
					state, //
					numberGeneratorDescriptor//
			), //
			(input) -> new Configuration(//
					input.readValue(settings), //
					input.readValue(state), //
					input.readValue(numberGeneratorDescriptor))//
	);

}
