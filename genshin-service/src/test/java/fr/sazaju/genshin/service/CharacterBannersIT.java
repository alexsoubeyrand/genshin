package fr.sazaju.genshin.service;

import static fr.sazaju.genshin.service.hateoas.assertion.HateoasMatcher.*;
import static fr.sazaju.genshin.service.hateoas.assertion.ResourceExtractor.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.sazaju.genshin.service.hateoas.HateoasClient;
import fr.sazaju.genshin.service.hateoas.Href.Method;
import fr.sazaju.genshin.service.hateoas.Resource;

class CharacterBannersIT {

	// TODO test permanent & weapon banners
	// TODO test self link everywhere

	private static String customRel(String rel) {
		return "http://localhost:8080/rels/" + rel;
	}

	private static final String WISH = customRel("wish");
	private static final String CHARACTER_BANNER = customRel("character-banner");
	private static final String SETTINGS = customRel("settings");
	private static final String MIHOYO = customRel("mihoyo");
	private static final String CONFIGURATION = customRel("configuration");
	private static final String NEXT_RUN = customRel("next-run");
	private static final String NEXT_MULTI = customRel("next-multi");

	private static final HateoasClient SERVICE = new HateoasClient();

	@Test
	void testRootHasCharacterBannerLink() {
		SERVICE.callRoot()//
				.getResource()//
				.assertThat(hasLink(CHARACTER_BANNER));
	}

	@Test
	void testCharacterBannerHasConfigurationLink() {
		SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.getResource()//
				.assertThat(hasLink(CONFIGURATION));
	}

	@Test
	void testConfigurationHasHalForm() {
		SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(CONFIGURATION)//
				.getResource()//
				.assertThat(hasJsonItem("_templates"));
	}

	@Test
	void testConfigurationHasPatch() {
		SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(CONFIGURATION)//
				.getResource()//
				.assertThat(jsonPath("_templates.default.method"), equalTo("patch"));
	}

	static Stream<Arguments> testConfigurationPatchUpdatesSingleValue() throws JsonProcessingException {
		return Stream.of(//
				arguments("probability4Stars", "settings.probability4Stars", 0.123f), //
				arguments("probability5Stars", "settings.probability5Stars", 0.456f), //
				arguments("probability4StarsWeaponCharacter", "settings.probability4StarsWeaponCharacter", 0.789f), //
				arguments("probability5StarsPermanentExclusive", "settings.probability5StarsPermanentExclusive",
						0.147f), //
				arguments("guaranty4Stars", "settings.guaranty4Stars", 123), //
				arguments("guaranty5Stars", "settings.guaranty5Stars", 456), //
				arguments("consecutiveWishesBelow4Stars", "state.consecutiveWishesBelow4Stars", 789), //
				arguments("consecutiveWishesBelow5Stars", "state.consecutiveWishesBelow5Stars", 147), //
				arguments("isExclusiveGuaranteedOnNext5Stars", "state.isExclusiveGuaranteedOnNext5Stars", true), //
				arguments("isExclusiveGuaranteedOnNext5Stars", "state.isExclusiveGuaranteedOnNext5Stars", false), //
				arguments("randomSeed", "numberGeneratorDescriptor.seed", 258), //
				arguments("randomValue", "numberGeneratorDescriptor.fixedValue", 0.5f), //
				arguments("randomList", "numberGeneratorDescriptor.values", List.of(0.1f, 0.5f, 0.9f))//
		);
	}

	@ParameterizedTest
	@MethodSource
	void testConfigurationPatchUpdatesSingleValue(String patchField, String confField, Object value)
			throws JsonProcessingException {
		testConfigurationPatchUpdatesField(patchField, value, confField, value);
	}

	static Stream<Arguments> testConfigurationPatchUpdatesField() throws JsonProcessingException {
		return Stream.of(//
				arguments("settingsKey", "UNLUCKY", "settings", Map.of(//
						"probability4Stars", 0f, //
						"probability5Stars", 0f, //
						"probability4StarsWeaponCharacter", 0f, //
						"probability5StarsPermanentExclusive", 0f, //
						"guaranty4Stars", Integer.MAX_VALUE, //
						"guaranty5Stars", Integer.MAX_VALUE//
				)), //
				arguments("settingsKey", "MIHOYO", "settings", Map.of(//
						"probability4Stars", 0.051f, //
						"probability5Stars", 0.006f, //
						"probability4StarsWeaponCharacter", 0.5f, //
						"probability5StarsPermanentExclusive", 0.5f, //
						"guaranty4Stars", 10, //
						"guaranty5Stars", 90//
				)), //
				arguments("stateKey", "FRESH", "state", Map.of(//
						"consecutiveWishesBelow4Stars", 0, //
						"consecutiveWishesBelow5Stars", 0, //
						"isExclusiveGuaranteedOnNext5Stars", false //
				))//
		);
	}

	@ParameterizedTest
	@MethodSource
	void testConfigurationPatchUpdatesField(String patchField, Object patchValue, String confField, Object confValue)
			throws JsonProcessingException {
		SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(CONFIGURATION)//
				.callResourceSelfLink(Method.PATCH, Map.of(patchField, patchValue))//
				.getResource()//
				.assertThat(jsonPath(confField), equalTo(confValue));
	}

	@Test
	void testConfigurationSuccessivePatchUpdatesAllFields() throws JsonProcessingException {
		SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(CONFIGURATION)//
				.callResourceSelfLink(Method.PATCH, Map.of("consecutiveWishesBelow4Stars", 123))//
				.callResourceSelfLink(Method.PATCH, Map.of("consecutiveWishesBelow5Stars", 456))//
				.callResourceSelfLink(Method.PATCH, Map.of("isExclusiveGuaranteedOnNext5Stars", true))//
				.getResource()//
				.assertThat(jsonPath("state"), equalTo(Map.of(//
						"consecutiveWishesBelow4Stars", 123, //
						"consecutiveWishesBelow5Stars", 456, //
						"isExclusiveGuaranteedOnNext5Stars", true //
				)));
	}

	@Test
	void testConfigurationHasNextRunLink() {
		SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(CONFIGURATION)//
				.getResource()//
				.assertThat(hasLink(NEXT_RUN));
	}

	@Test
	void testConfigurationWorstRunHas3StarsNonExclusiveWeapon() throws JsonProcessingException {
		SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(CONFIGURATION)//
				.callResourceSelfLink(Method.PATCH, Map.of(//
						"settingsKey", "UNLUCKY", //
						"stateKey", "FRESH"
				))//
				.callResourceLink(NEXT_RUN)//
				.getResource()//
				// TODO Exclude HAL fields
				.assertThat(equalTo(Map.of(//
						"stars", 3, //
						"type", "WEAPON", //
						"isExclusive", false//
				)));
	}

	// TODO Test run content and links

	@Test
	void testCharactersBannerConfigurationHasNextMultiLink() {
		SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(CONFIGURATION)//
				.getResource()//
				.assertThat(hasLink(NEXT_MULTI));
	}

	// TODO Test multi content and links

	// FIXME

	@Test
	void testCharacterBannerSettingsHavePostedData() {
		SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(SETTINGS)//
				// TODO POST data
				.getResource()//
		// TODO retrieve link
		// TODO GET link
		// TODO assert data corresponds
		;
		throw new RuntimeException("Not implemented yet");
	}

	@Test
	void testCharacterBannerSettingsHaveMihoyoLink() {
		SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(SETTINGS)//
				.getResource()//
				.assertThat(hasLink(MIHOYO));
	}

	@ParameterizedTest
	@MethodSource("allProperties")
	void testCharacterBannerWithMihoyoSettingsHasExpectedProperty(String property) {
		SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(SETTINGS)//
				.callResourceLink(MIHOYO)//
				.getResource()//
				.assertThat(hasItem(property));
	}

	@Test
	void testCharacterBannerWithMihoyoSettingsHaveMihoyoProperties() {
		Resource profile = SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(SETTINGS)//
				.callResourceLink(MIHOYO)//
				.getResource();

		profile.assertThat(jsonPath("probability4Stars"), equalTo(0.051f));
		profile.assertThat(jsonPath("probability4StarsWeaponCharacter"), equalTo(0.5f));
		profile.assertThat(jsonPath("probability5Stars"), equalTo(0.006f));
		profile.assertThat(jsonPath("probability5StarsPermanentExclusive"), equalTo(0.5f));
		profile.assertThat(jsonPath("guaranty4Stars"), equalTo(10));
		profile.assertThat(jsonPath("guaranty5Stars"), equalTo(90));
	}

	// XXX /wishes/{type}/profiles/{profile}/wish
	// type := permanent | weaponEvent | characterEvent
	// profile := data/{data} | keys/{profileKey}

	@Disabled
	@Test
	void testCharacterWishWithMihoyoSettingsHasCorrectResult() {
		// TODO Parameterize
		double random = 1.0;

		// TODO Parameterize
		int counterBelow4Stars = 0;
		int counterBelow5Stars = 0;
		boolean guarantyExclusiveOnNext5Stars = false;

		int expectedStars = 3;
		String expectedType = "weapon";
		boolean expectedExclusive = false;

		Resource wish = SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(SETTINGS)//
				.callResourceLink(MIHOYO)//
				.getResource().getLink(WISH).href().get()// TODO Manage args
				.getResource();

		wish.assertThat(jsonPath("stars"), equalTo(expectedStars));
		wish.assertThat(jsonPath("type"), equalTo(expectedType));
		wish.assertThat(jsonPath("isExclusive"), equalTo(expectedExclusive));
	}

	// TODO Test series of wishes

}
