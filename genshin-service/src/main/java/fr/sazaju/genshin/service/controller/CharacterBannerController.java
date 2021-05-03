package fr.sazaju.genshin.service.controller;

import static fr.sazaju.genshin.service.controller.Linker.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.sazaju.genshin.service.controller.coder.ConfigurationCoder;
import fr.sazaju.genshin.service.controller.coder.ConfigurationDefinition.Configuration;
import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.NumberGeneratorDescriptor;
import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.RandomNGDescriptor;
import fr.sazaju.genshin.service.model.ConfPatch;
import fr.sazaju.genshin.simulator.NumberGenerator;
import fr.sazaju.genshin.simulator.wish.Settings;
import fr.sazaju.genshin.simulator.wish.State;
import fr.sazaju.genshin.simulator.wish.Wish;

@Controller
// TODO Check HAL specifications: https://stateless.group/hal_specification.html
// TODO Check media types RFC: https://tools.ietf.org/html/rfc6838
//@EnableHypermediaSupport(type = HypermediaType.HAL)
//@EnableHypermediaSupport(type = HypermediaType.HAL_FORMS)
//@RequestMapping(value = "/banners/character", produces = "application/hal+json")
@RequestMapping(value = "/banners/character", produces = "application/prs.hal-forms+json")
public class CharacterBannerController {

	@GetMapping
	@ResponseBody
	public CollectionModel<?> getBanner() {
		return allLinks().decorateCharactersBanner(CollectionModel.of(List.of()));
	}

	@GetMapping("/configuration")
	@ResponseBody
	public EntityModel<Configuration> getConfiguration() {
		Configuration configuration = getDefaultConfiguration();
		return allLinks().decorateCharactersBannerWishConfiguration(//
				EntityModel.of(configuration), //
				this::serializeConfiguration);
	}

	// TODO Add links to change settings
	// TODO Add links to change profile
	// TODO Add links to change random
	@GetMapping("/configuration/{serial}")
	@ResponseBody
	public EntityModel<Configuration> getConfiguration(@PathVariable String serial) {
		Configuration configuration = deserializeConfiguration(serial);
		return allLinks().decorateCharactersBannerWishConfiguration(//
				EntityModel.of(configuration), //
				this::serializeConfiguration);
	}

	@PatchMapping("/configuration/{serial}")
	@ResponseBody
	public EntityModel<Configuration> patchConfiguration(@PathVariable String serial, @RequestBody ConfPatch patch) {
		Configuration configuration = deserializeConfiguration(serial);
		configuration = patch.apply(configuration);
		return allLinks().decorateCharactersBannerWishConfiguration(//
				EntityModel.of(configuration), //
				this::serializeConfiguration);
	}

	@GetMapping("/run/{serial}")
	@ResponseBody
	public EntityModel<Wish> getRun(@PathVariable String serial) {
		Configuration startingConfiguration = deserializeSimulator(serial);
		Result<Wish> run = getRunHelper(//
				startingConfiguration.settings, //
				startingConfiguration.state, //
				startingConfiguration.numberGeneratorDescriptor);
		return allLinks().decorateCharactersBannerWish(//
				EntityModel.of(run.result), //
				this::serializeSimulator, startingConfiguration, run.nextConfiguration);
	}

	@GetMapping("/multi/{serial}")
	@ResponseBody
	public CollectionModel<Wish> getMulti(@PathVariable String serial) {
		Configuration startingConfiguration = deserializeSimulator(serial);
		Result<List<Wish>> runs = getMultiHelper(//
				startingConfiguration.settings, //
				startingConfiguration.state, //
				startingConfiguration.numberGeneratorDescriptor);
		return allLinks().decorateCharactersBannerMulti(//
				CollectionModel.of(runs.result), //
				this::serializeSimulator, startingConfiguration, runs.nextConfiguration);
	}

	private <T extends NumberGenerator> Result<Wish> getRunHelper(Settings settings, State startingState,
			NumberGeneratorDescriptor<T> descriptor) {
		T generator = descriptor.createNumberGenerator();
		return Wish.createStream(settings, startingState, Stream.generate(() -> generator.nextFloat()))//
				.map(run -> {
					NumberGeneratorDescriptor<T> nextDescriptor = descriptor.prepareNextDescriptor(generator);
					Configuration nextConfiguration = new Configuration(settings, run.nextState, nextDescriptor);
					return new Result<>(run.wish, nextConfiguration);
				})//
				.findFirst().orElseThrow();
	}

	private <T extends NumberGenerator> Result<List<Wish>> getMultiHelper(Settings settings, State startingState,
			NumberGeneratorDescriptor<T> descriptor) {
		T numberGenerator = descriptor.createNumberGenerator();
		List<Wish> wishes = new LinkedList<>();
		State[] currentState = { startingState };
		Wish.createStream(settings, startingState, Stream.generate(() -> numberGenerator.nextFloat()))//
				.limit(10)// multi = 10 runs
				.forEach(run -> {
					wishes.add(run.wish);
					currentState[0] = run.nextState;
				});

		State nextState = currentState[0];
		NumberGeneratorDescriptor<T> nextDescriptor = descriptor.prepareNextDescriptor(numberGenerator);
		Configuration nextConfiguration = new Configuration(settings, nextState, nextDescriptor);
		return new Result<>(wishes, nextConfiguration);
	}

	private String serializeConfiguration(Configuration configuration) {
		return ConfigurationCoder.generateShortestSerial(configuration);
	}

	private Configuration deserializeConfiguration(String serial) {
		try {
			return ConfigurationCoder.fromSerial(serial).decode(serial);
		} catch (IOException cause) {
			throw new RuntimeException(cause);
		}
	}

	private String serializeSimulator(Configuration simulator) {
		return ConfigurationCoder.generateShortestSerial(simulator);
	}

	private Configuration deserializeSimulator(String serial) {
		try {
			return ConfigurationCoder.fromSerial(serial).decode(serial);
		} catch (IOException cause) {
			throw new RuntimeException(cause);
		}
	}

	private State getDefaultState() {
		return State.createFresh();
	}

	private Settings getDefaultSettings() {
		return Settings.createMihoyoSettings();
	}

	private Configuration getDefaultConfiguration() {
		return new Configuration(getDefaultSettings(), getDefaultState(), new RandomNGDescriptor(0));
	}

	private class Result<T> {
		public final T result;
		public final Configuration nextConfiguration;

		public Result(T result, Configuration nextConfiguration) {
			this.result = result;
			this.nextConfiguration = nextConfiguration;
		}
	}

}
