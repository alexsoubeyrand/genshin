package fr.sazaju.genshin.service;

import static fr.sazaju.genshin.service.hateoas.assertion.HateoasMatcher.*;
import static fr.sazaju.genshin.service.hateoas.assertion.ResourceExtractor.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

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

	private static final HateoasClient SERVICE = new HateoasClient();

	private static final String CHARACTER_BANNER = customRel("character-banner");
	private static final String CONFIGURATION = customRel("configuration");
	private static final String NEXT_RUN = customRel("next-run");
	private static final String NEXT_MULTI = customRel("next-multi");

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
	// TODO Test form
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

	static Stream<Arguments> testConfigurationRunReturnsCorrectResult() throws JsonProcessingException {
		return Stream.of(//
				arguments(confWithProbabilities(0, 0, 0, 0), run3Stars), //
				arguments(confWithProbabilities(1, 0, 0, 0), run4StarsWeapon), //
				arguments(confWithProbabilities(1, 1, 0, 0), run4StarsCharacter), //
				arguments(confWithProbabilities(0, 0, 1, 0), run5StarsPermanent), //
				arguments(confWithProbabilities(0, 0, 1, 1), run5StarsExclusive), //
				arguments(confWithGuaranty(true, 0, false, 0), run4StarsWeapon), //
				arguments(confWithGuaranty(true, 1, false, 0), run4StarsCharacter), //
				arguments(confWithGuaranty(false, 0, true, 0), run5StarsPermanent), //
				arguments(confWithGuaranty(false, 0, true, 1), run5StarsExclusive) //
		);
	}

	@ParameterizedTest
	@MethodSource
	void testConfigurationRunReturnsCorrectResult(Map<String, Integer> configurationPatch, Map<String, Object> runData)
			throws JsonProcessingException {
		SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(CONFIGURATION)//
				.callResourceSelfLink(Method.PATCH, configurationPatch)//
				.callResourceLink(NEXT_RUN)//
				.getResource()//
				.assertThat(ignoringHalFormFields(), equalTo(runData));
	}

	@Test
	void testConfigurationHasNextMultiLink() {
		SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(CONFIGURATION)//
				.getResource()//
				.assertThat(hasLink(NEXT_MULTI));
	}

	static Stream<Arguments> testConfigurationSuccessiveMultisReturnsCorrectResults() throws JsonProcessingException {
		return Stream.of(//
				// Identical successive multis
				arguments(confOnly3Stars(), repeat(3, multiOf(run3Stars))), //
				arguments(conf4StarsEvery(5),
						repeat(3, multiOf(run3Stars, exceptIndexes(List.of(4, 9), run4StarsWeapon)))), //
				arguments(conf4StarsAnd5StarsEvery(3, 5), repeat(3, multiOf(run3Stars, //
						exceptIndex(2, run4StarsWeapon), //
						exceptIndex(4, run5StarsPermanent), //
						exceptIndex(7, run4StarsWeapon), //
						exceptIndex(9, run5StarsExclusive)//
				))), //
				// Different successive multis
				arguments(conf4StarsEvery(7), List.of(//
						multiOf(run3Stars, exceptIndex(6, run4StarsWeapon)), //
						multiOf(run3Stars, exceptIndex(3, run4StarsWeapon)), //
						multiOf(run3Stars, exceptIndexes(List.of(0, 7), run4StarsWeapon))//
				)) //
		);
	}

	@ParameterizedTest
	@MethodSource
	void testConfigurationSuccessiveMultisReturnsCorrectResults(Map<String, Integer> confPatch,
			List<List<Map<String, Object>>> multiSeries) throws JsonProcessingException {
		Resource resource = SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(CONFIGURATION)//
				.callResourceSelfLink(Method.PATCH, confPatch)//
				.getResource();

		for (List<Map<String, Object>> multi2 : multiSeries) {
			resource = resource.callLink(NEXT_MULTI).getResource();
			resource.assertThat(jsonPath("_embedded.wishList"), equalTo(multi2));
		}
	}

	@SuppressWarnings("serial")
	static Stream<Arguments> testConfigurationSuccessiveRunsReturnsCorrectResults() throws JsonProcessingException {
		// Rely on multis cases to check we get the same thing
		return testConfigurationSuccessiveMultisReturnsCorrectResults()//
				.map(args -> {
					@SuppressWarnings("unchecked")
					List<List<Map<String, Object>>> multiSeries = (List<List<Map<String, Object>>>) args.get()[1];
					List<Map<String, Object>> runSeries = multiSeries.stream()//
							.flatMap(multi -> multi.stream())//
							.collect(toList());
					runSeries = Collections.unmodifiableList(new ArrayList<>(runSeries) {
						@Override
						public String toString() {
							return formatRuns(this);
						}
					});
					return arguments(args.get()[0], runSeries);
				});
	}

	@ParameterizedTest
	@MethodSource
	void testConfigurationSuccessiveRunsReturnsCorrectResults(Map<String, Integer> confPatch,
			List<Map<String, Object>> runSeries) throws JsonProcessingException {
		Resource resource = SERVICE.callRoot()//
				.callResourceLink(CHARACTER_BANNER)//
				.callResourceLink(CONFIGURATION)//
				.callResourceSelfLink(Method.PATCH, confPatch)//
				.getResource();

		for (Map<String, Object> run : runSeries) {
			resource = resource.callLink(NEXT_RUN).getResource();
			resource.assertThat(ignoringHalFormFields(), equalTo(run));
		}
	}

	static Stream<List<String>> testCharacterBannerResourceHasSelfLink() {
		return Stream.of(//
				List.of(), // Banner itself
				List.of(CONFIGURATION), //
				List.of(CONFIGURATION, NEXT_RUN), //
				List.of(CONFIGURATION, NEXT_MULTI), //
				List.of(CONFIGURATION, NEXT_RUN, NEXT_MULTI), //
				List.of(CONFIGURATION, NEXT_MULTI, NEXT_RUN) //
		);
	}

	@ParameterizedTest
	@MethodSource
	void testCharacterBannerResourceHasSelfLink(List<String> relsSequenceFromBanner) {
		Resource resource = SERVICE.callRoot().callResourceLink(CHARACTER_BANNER).getResource();
		for (String rel : relsSequenceFromBanner) {
			resource = resource.callLink(rel).getResource();
		}
		resource.assertThat(hasSelfLink());
	}

	@SuppressWarnings("serial")
	private static Map<String, Object> run(int stars, String type, boolean isExclusive) {
		return new HashMap<>(Map.of("stars", stars, "type", type, "isExclusive", isExclusive)) {
			public String toString() {
				return String.format("%s☆ %s%s", stars, type, isExclusive ? "_EX" : "");
			};
		};
	}

	private static Map<String, Object> run3Stars = run(3, "WEAPON", false);
	private static Map<String, Object> run4StarsWeapon = run(4, "WEAPON", false);
	private static Map<String, Object> run4StarsCharacter = run(4, "CHARACTER", false);
	private static Map<String, Object> run5StarsPermanent = run(5, "CHARACTER", false);
	private static Map<String, Object> run5StarsExclusive = run(5, "CHARACTER", true);

	private static MultiModifier exceptIndex(int index, Map<String, Object> run) {
		return multi -> multi.set(index, run);
	}

	private static MultiModifier exceptIndexes(List<Integer> indexes, Map<String, Object> run) {
		return multi -> indexes.forEach(index -> multi.set(index, run));
	}

	interface MultiModifier {
		void adapt(List<Map<String, Object>> multi);
	}

	private static List<Map<String, Object>> multiOf(Map<String, Object> defaultRun, MultiModifier... modifiers) {
		List<Map<String, Object>> baseMulti = range(0, 10).mapToObj(i -> defaultRun).collect(toList());
		@SuppressWarnings("serial")
		List<Map<String, Object>> modifiableMulti = new ArrayList<>(baseMulti) {
			public String toString() {
				return formatRuns(this);
			}
		};
		for (MultiModifier modifier : modifiers) {
			modifier.adapt(modifiableMulti);
		}
		return Collections.unmodifiableList(modifiableMulti);
	}

	private static String formatRuns(List<Map<String, Object>> multi) {
		return multi.stream()//
				.map(run -> Map.entry(1, run))// Associate a counter initialized to 1
				.map(entry -> new LinkedList<>(List.of(entry)))// Wrap in list to allow reduce step
				.reduce((reducedList, nextList) -> {// Merge single item nextList into reducedList
					Entry<Integer, Map<String, Object>> nextEntry = nextList.getFirst();// Only one here
					Entry<Integer, Map<String, Object>> lastEntry = reducedList.getLast();
					Map<String, Object> nextRun = nextEntry.getValue();
					Map<String, Object> lastRun = lastEntry.getValue();
					if (nextRun.equals(lastRun)) {
						// Increase the counter of the last run
						int increasedCounter = lastEntry.getKey() + 1;
						int lastIndex = reducedList.size() - 1;
						reducedList.set(lastIndex, Map.entry(increasedCounter, lastRun));
					} else {
						// Add the next run separately
						reducedList.add(nextEntry);
					}
					return reducedList;
				})//
				.get().stream()// Retrieve the stream of entries
				.map(entry -> {// Format each entry
					Integer counter = entry.getKey();
					Map<String, Object> run = entry.getValue();
					if (counter > 1) {
						return run + " x " + counter;
					} else {
						return run;
					}
				})//
				.collect(toList()).toString();
	}

	private static Map<String, Integer> confOnly3Stars() {
		return Map.of(//
				"probability4Stars", 0, //
				"probability4StarsWeaponCharacter", 0, //
				"consecutiveWishesBelow4Stars", 0, //
				"guaranty4Stars", Integer.MAX_VALUE, //

				"probability5Stars", 0, //
				"probability5StarsPermanentExclusive", 0, //
				"consecutiveWishesBelow5Stars", 0, //
				"guaranty5Stars", Integer.MAX_VALUE//
		);
	}

	private static Map<String, Integer> conf4StarsAnd5StarsEvery(int frequency4Stars, int frequency5Stars) {
		return Map.of(//
				"probability4Stars", 0, //
				"probability4StarsWeaponCharacter", 0, //
				"consecutiveWishesBelow4Stars", 0, //
				"guaranty4Stars", frequency4Stars, //

				"probability5Stars", 0, //
				"probability5StarsPermanentExclusive", 0, //
				"consecutiveWishesBelow5Stars", 0, //
				"guaranty5Stars", frequency5Stars//
		);
	}

	private static Map<String, Integer> conf4StarsEvery(int frequency) {
		return Map.of(//
				"probability4Stars", 0, //
				"probability4StarsWeaponCharacter", 0, //
				"consecutiveWishesBelow4Stars", 0, //
				"guaranty4Stars", frequency, //

				"probability5Stars", 0, //
				"probability5StarsPermanentExclusive", 0, //
				"consecutiveWishesBelow5Stars", 0, //
				"guaranty5Stars", Integer.MAX_VALUE//
		);
	}

	private static List<List<Map<String, Object>>> repeat(int repetitions, List<Map<String, Object>> multi) {
		return range(0, repetitions).mapToObj(i -> multi).collect(toList());
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

	private static String customRel(String rel) {
		return "http://localhost:8080/rels/" + rel;
	}
}
