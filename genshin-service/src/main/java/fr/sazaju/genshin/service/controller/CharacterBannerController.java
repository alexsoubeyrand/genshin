package fr.sazaju.genshin.service.controller;

import static fr.sazaju.genshin.service.controller.Linker.*;
import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.NumberGeneratorDescriptor;
import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.RandomNGDescriptor;
import fr.sazaju.genshin.service.controller.coder.ProfileCoder;
import fr.sazaju.genshin.service.controller.coder.SettingsCoder;
import fr.sazaju.genshin.service.controller.coder.SimulatorCoder;
import fr.sazaju.genshin.service.controller.coder.SimulatorDefinition.Simulator;
import fr.sazaju.genshin.simulator.NumberGenerator;
import fr.sazaju.genshin.simulator.wish.Profile;
import fr.sazaju.genshin.simulator.wish.Settings;
import fr.sazaju.genshin.simulator.wish.Wish;
import fr.sazaju.genshin.simulator.wish.Wish.Generator;

@Controller
@RequestMapping(value = "/banners/character", produces = "application/hal+json")
public class CharacterBannerController {

	@GetMapping
	@ResponseBody
	public CollectionModel<?> getBanner() {
		return allLinks().decorateCharactersBanner(CollectionModel.of(List.of()));
	}

	@GetMapping("/wishes")
	@ResponseBody
	public CollectionModel<Wish> getWishes() {
		Simulator simulator = getDefaultSimulator();
		return getWishes(//
				simulator.settings, //
				simulator.profile, //
				simulator.numberGeneratorDescriptor);
	}

	// TODO Add links to change settings
	// TODO Add links to change profile
	// TODO Add links to change random
	@GetMapping("/wishes/{serial}")
	@ResponseBody
	public CollectionModel<Wish> getWishes(@PathVariable String serial) {
		Simulator simulator = deserializeSimulator(serial);
		return getWishes(//
				simulator.settings, //
				simulator.profile, //
				simulator.numberGeneratorDescriptor);
	}

	private <T extends NumberGenerator> CollectionModel<Wish> getWishes(Settings settings, Profile profile,
			NumberGeneratorDescriptor<T> descriptor) {
		T numberGenerator = descriptor.createNumberGenerator();
		int runsCount = Math.min(10, descriptor.wishesCount);// Limit to 10 runs at once (multi)

		Generator wishGenerator = new Wish.Generator(settings, profile);
		List<Wish> wishes = IntStream.range(0, runsCount)//
				.mapToObj(i -> wishGenerator.nextWish(numberGenerator.nextFloat()))//
				.collect(toList());

		Profile nextProfile = wishGenerator.getCurrentProfile();
		NumberGeneratorDescriptor<T> nextDescriptor = descriptor.prepareNextDescriptor(numberGenerator,
				descriptor.wishesCount - runsCount);

		Simulator simulator = new Simulator(settings, profile, descriptor);
		Simulator nextSimulator = new Simulator(settings, nextProfile, nextDescriptor);
		return allLinks().decorateCharactersBannerWishes(//
				CollectionModel.of(wishes), //
				this::serializeSimulator, simulator, nextSimulator, //
				this::serializeSettings, this::serializeProfile);
	}

	@GetMapping("/settings")
	@ResponseBody
	public EntityModel<Settings> getSettings() {
		Settings settings = getDefaultSettings();
		return allLinks().decorateCharactersBannerWishSettings(//
				EntityModel.of(settings), //
				this::serializeSettings, Settings.createMihoyoSettings());
	}

	@GetMapping("/settings/{serial}")
	@ResponseBody
	public EntityModel<Settings> getSettings(@PathVariable String serial) {
		Settings settings = deserializeSettings(serial);
		return allLinks().decorateCharactersBannerWishSettings(//
				EntityModel.of(settings), //
				this::serializeSettings, Settings.createMihoyoSettings());
	}

	@GetMapping("/profile")
	@ResponseBody
	public EntityModel<Profile> getProfile() {
		Profile defaultProfile = getDefaultProfile();
		return allLinks().decorateCharactersBannerWishProfile(//
				EntityModel.of(defaultProfile), //
				this::serializeProfile, defaultProfile);
	}

	@GetMapping("/profile/{serial}")
	@ResponseBody
	public EntityModel<Profile> getProfile(@PathVariable String serial) {
		Profile profile = deserializeProfile(serial);
		return allLinks().decorateCharactersBannerWishProfile(//
				EntityModel.of(profile), //
				this::serializeProfile, getDefaultProfile());
	}

	private String serializeProfile(Profile profile) {
		return ProfileCoder.generateShortestSerial(profile);
	}

	private Profile deserializeProfile(String serial) {
		try {
			return ProfileCoder.fromSerial(serial).decode(serial);
		} catch (IOException cause) {
			throw new RuntimeException(cause);
		}
	}

	private String serializeSettings(Settings settings) {
		return SettingsCoder.generateShortestSerial(settings);
	}

	private Settings deserializeSettings(String serial) {
		try {
			return SettingsCoder.fromSerial(serial).decode(serial);
		} catch (IOException cause) {
			throw new RuntimeException(cause);
		}
	}

	private String serializeSimulator(Simulator simulator) {
		return SimulatorCoder.generateShortestSerial(simulator);
	}

	private Simulator deserializeSimulator(String serial) {
		try {
			return SimulatorCoder.fromSerial(serial).decode(serial);
		} catch (IOException cause) {
			throw new RuntimeException(cause);
		}
	}

	private Profile getDefaultProfile() {
		return Profile.createFreshProfile();
	}

	private Settings getDefaultSettings() {
		return Settings.createMihoyoSettings();
	}

	private Simulator getDefaultSimulator() {
		return new Simulator(getDefaultSettings(), getDefaultProfile(), new RandomNGDescriptor(0, 100));
	}
}
