package fr.sazaju.genshin.service.controller;

import static fr.sazaju.genshin.service.controller.Linker.*;

import java.io.IOException;
import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.sazaju.genshin.service.controller.coder.ProfileCoder;
import fr.sazaju.genshin.service.controller.coder.SettingsCoder;
import fr.sazaju.genshin.simulator.wish.Profile;
import fr.sazaju.genshin.simulator.wish.Settings;
import fr.sazaju.genshin.simulator.wish.Wish;
import fr.sazaju.genshin.simulator.wish.Wish.Generator;

@Controller
@RequestMapping(value = "/banners", produces = "application/hal+json")
public class BannerController {

	@GetMapping
	@ResponseBody
	public CollectionModel<?> getBanners() {
		return allLinks().decorateBannerCollection(CollectionModel.of(List.of()));
	}

	@GetMapping("/characters")
	@ResponseBody
	public CollectionModel<?> getCharactersBanner() {
		return allLinks().decorateCharactersBanner(CollectionModel.of(List.of()));
	}

	@GetMapping("/characters/wish")
	@ResponseBody
	public EntityModel<Wish> getCharactersBannerWish() {
		// TODO (De)Serialize settings + profile + random
		Settings settings = getDefaultSettings();
		Profile profile = getDefaultProfile();
		float randomValue = getDefaultRandom();
		Generator generator = new Wish.Generator(settings, profile);
		Wish wish = generator.run(randomValue);
		Profile newProfile = generator.getCurrentProfile();

		return allLinks().decorateCharactersBannerWish(//
				EntityModel.of(wish), //
				this::serializeSettings, settings, //
				this::serializeProfile, profile, newProfile);
	}

	private float getDefaultRandom() {
		return 1.0f;
	}

	private Profile getDefaultProfile() {
		return Profile.createFreshProfile();
	}

	@GetMapping("/characters/settings")
	@ResponseBody
	public EntityModel<Settings> getCharactersBannerWishSettings() {
		Settings settings = getDefaultSettings();
		String defaultSerial = serializeSettings(settings);
		return allLinks().decorateCharactersBannerWishSettings(EntityModel.of(settings), defaultSerial);
	}

	private Settings getDefaultSettings() {
		return Settings.createMihoyoSettings();
	}

	@GetMapping("/characters/settings/{serial}")
	@ResponseBody
	public EntityModel<Settings> getCharactersBannerWishSettings(@PathVariable String serial) {
		Settings settings = deserializeSettings(serial);
		return allLinks().decorateCharactersBannerWishSettings(EntityModel.of(settings), serial);
	}

	@GetMapping("/characters/profile")
	@ResponseBody
	public EntityModel<Profile> getCharactersBannerWishProfile() {
		Profile profile = getDefaultProfile();
		String defaultSerial = serializeProfile(profile);
		return allLinks().decorateCharactersBannerWishProfile(EntityModel.of(profile), defaultSerial);
	}

	@GetMapping("/characters/profile/{serial}")
	@ResponseBody
	public EntityModel<Profile> getCharactersBannerWishProfile(@PathVariable String serial) {
		Profile profile = deserializeProfile(serial);
		return allLinks().decorateCharactersBannerWishProfile(EntityModel.of(profile), serializeProfile(profile));
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
}
