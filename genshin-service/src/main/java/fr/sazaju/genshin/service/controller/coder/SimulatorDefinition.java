package fr.sazaju.genshin.service.controller.coder;

import java.util.List;

import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.NumberGeneratorDescriptor;
import fr.sazaju.genshin.simulator.wish.Profile;
import fr.sazaju.genshin.simulator.wish.Settings;

public class SimulatorDefinition {

	public static class Simulator {
		public final Settings settings;
		public final Profile profile;
		public final NumberGeneratorDescriptor<?> numberGeneratorDescriptor;

		public Simulator(Settings settings, Profile profile, NumberGeneratorDescriptor<?> numberGeneratorDescriptor) {
			this.settings = settings;
			this.profile = profile;
			this.numberGeneratorDescriptor = numberGeneratorDescriptor;
		}
	}

	private static final Property<Simulator, Settings> settings = //
			Property.onDefinition(SettingsDefinition.V1, simulator -> simulator.settings);
	private static final Property<Simulator, Profile> profile = //
			Property.onDefinition(ProfileDefinition.V1, simulator -> simulator.profile);
	private static final Property<Simulator, NumberGeneratorDescriptor<?>> numberGeneratorDescriptor = //
			Property.onDefinition(NumberGeneratorDescriptorDefinition.V1,
					simulator -> simulator.numberGeneratorDescriptor);

	public static final Definition<Simulator> V1 = Definition.onProperties(//
			List.of(//
					settings, //
					profile, //
					numberGeneratorDescriptor//
			), //
			(input) -> new Simulator(//
					input.readValue(settings), //
					input.readValue(profile), //
					input.readValue(numberGeneratorDescriptor))//
	);

}
