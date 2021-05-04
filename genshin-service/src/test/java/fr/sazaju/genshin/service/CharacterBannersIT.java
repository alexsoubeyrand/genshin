package fr.sazaju.genshin.service;

import static fr.sazaju.genshin.service.hateoas.assertion.HateoasMatcher.*;
import static fr.sazaju.genshin.service.hateoas.assertion.ResourceExtractor.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.sazaju.genshin.service.hateoas.HateoasClient;
import fr.sazaju.genshin.service.hateoas.Href.Method;

class CharacterBannersIT {

	// TODO test permanent & weapon banners
	// TODO test self link everywhere

	private static String customRel(String rel) {
		return "http://localhost:8080/rels/" + rel;
	}

	private static final String CHARACTER_BANNER = customRel("character-banner");
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

	private static Map<String, Object> confWithProbabilities(//
			double proba4Stars, double proba4StarsCharacter, //
			double proba5Stars, double proba5StarsExclusive) {
		int counter = 0;
		int guaranty = Integer.MAX_VALUE;// higher than counter to not activate
		return Map.of(//
				"probability4Stars", proba4Stars, //
				"probability4StarsWeaponCharacter", proba4StarsCharacter, //
				"consecutiveWishesBelow4Stars", counter, //
				"guaranty4Stars", guaranty, //

				"probability5Stars", proba5Stars, //
				"probability5StarsPermanentExclusive", proba5StarsExclusive, //
				"consecutiveWishesBelow5Stars", counter, //
				"guaranty5Stars", guaranty//
		);
	}

	private static Map<String, Object> confWithGuaranty(//
			boolean guaranty4Stars, double proba4StarsCharacter, //
			boolean guaranty5Stars, double proba5StarsExclusive) {
		return Map.of(//
				"probability4Stars", (double) 0, //
				"probability4StarsWeaponCharacter", proba4StarsCharacter, //
				"consecutiveWishesBelow4Stars", 0, //
				"guaranty4Stars", guaranty4Stars ? 1 : Integer.MAX_VALUE, //

				"probability5Stars", (double) 0, //
				"probability5StarsPermanentExclusive", proba5StarsExclusive, //
				"consecutiveWishesBelow5Stars", 0, //
				"guaranty5Stars", guaranty5Stars ? 1 : Integer.MAX_VALUE);
	}

	static Stream<Arguments> testConfigurationForRandomRunReturnsCorrectResult() throws JsonProcessingException {
		return Stream.of(//
				arguments(confWithProbabilities(0, 0, 0, 0), run3StarsNonExclusiveWeapon), //
				arguments(confWithProbabilities(1, 0, 0, 0), run4StarsNonExclusiveWeapon), //
				arguments(confWithProbabilities(1, 1, 0, 0), run4StarsNonExclusiveCharacter), //
				arguments(confWithProbabilities(0, 0, 1, 0), run5StarsNonExclusiveCharacter), //
				arguments(confWithProbabilities(0, 0, 1, 1), run5StarsExclusiveCharacter), //
				arguments(confWithGuaranty(true, 0, false, 0), run4StarsNonExclusiveWeapon), //
				arguments(confWithGuaranty(true, 1, false, 0), run4StarsNonExclusiveCharacter), //
				arguments(confWithGuaranty(false, 0, true, 0), run5StarsNonExclusiveCharacter), //
				arguments(confWithGuaranty(false, 0, true, 1), run5StarsExclusiveCharacter) //
		);
	}

	@ParameterizedTest
	@MethodSource
	void testConfigurationForRandomRunReturnsCorrectResult(Map<String, Integer> configurationPatch,
			Map<String, Object> runData) throws JsonProcessingException {
		SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(CONFIGURATION)//
				.callResourceSelfLink(Method.PATCH, configurationPatch)//
				.callResourceLink(NEXT_RUN)//
				.getResource()//
				.assertThat(ignoringHalFormFields(), equalTo(runData));
	}

	@Test
	void testCharactersBannerConfigurationHasNextMultiLink() {
		SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(CONFIGURATION)//
				.getResource()//
				.assertThat(hasLink(NEXT_MULTI));
	}

	@Test
	void testConfigurationForRandomMultiReturnsCorrectResult() throws JsonProcessingException {
		SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(CONFIGURATION)//
				.callResourceSelfLink(Method.PATCH, Map.of(//
						"probability4Stars", 0, //
						"probability4StarsWeaponCharacter", 0, //
						"consecutiveWishesBelow4Stars", 0, //
						"guaranty4Stars", 999, //

						"probability5Stars", 0, //
						"probability5StarsPermanentExclusive", 0, //
						"consecutiveWishesBelow5Stars", 0, //
						"guaranty5Stars", 999//
				))//
				.callResourceLink(NEXT_MULTI)//
				.getResource()//
				// TODO Cover different cases
				.assertThat(jsonPath("_embedded.wishList"), equalTo(List.of(//
						run3StarsNonExclusiveWeapon, //
						run3StarsNonExclusiveWeapon, //
						run3StarsNonExclusiveWeapon, //
						run3StarsNonExclusiveWeapon, //
						run3StarsNonExclusiveWeapon, //
						run3StarsNonExclusiveWeapon, //
						run3StarsNonExclusiveWeapon, //
						run3StarsNonExclusiveWeapon, //
						run3StarsNonExclusiveWeapon, //
						run3StarsNonExclusiveWeapon//
				)));
	}

	// TODO Test multi content
	// TODO Test run links
	// TODO Test multi links
	
	private static Map<String, Object> run(int stars, String type, boolean isExclusive) {
		return Map.of("stars", stars, "type", type, "isExclusive", isExclusive);
	}

	private static Map<String, Object> run3StarsNonExclusiveWeapon = run(3, "WEAPON", false);
	private static Map<String, Object> run4StarsNonExclusiveWeapon = run(4, "WEAPON", false);
	private static Map<String, Object> run4StarsNonExclusiveCharacter = run(4, "CHARACTER", false);
	private static Map<String, Object> run5StarsNonExclusiveCharacter = run(5, "CHARACTER", false);
	private static Map<String, Object> run5StarsExclusiveCharacter = run(5, "CHARACTER", true);
}
