package fr.sazaju.genshin.service;

import static fr.sazaju.genshin.service.Links.Packs.*;
import static fr.sazaju.genshin.service.hateoas.assertion.HateoasMatcher.*;
import static fr.sazaju.genshin.service.hateoas.assertion.ResourceExtractor.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.service.hateoas.Client;
import fr.sazaju.genshin.service.hateoas.CollectionResource;
import fr.sazaju.genshin.service.hateoas.Hateoas;
import fr.sazaju.genshin.service.hateoas.Resource;
import fr.sazaju.genshin.service.hateoas.Response;

class PackIT {

	private static final Client SERVICE = Hateoas.createClient("/genshin");
	
	public static final String EUROS = "euros";
	public static final String CRISTALS = "cristals";

	@Test
	void testRootHasPacksLink() {
		SERVICE.callRoot()//
				.getResource()//
				.assertThat(hasLink(PACKS));
	}

	@ParameterizedTest
	@MethodSource("packsResponse")
	void testPacksHasFirstOrderLink(Response response) {
		response.getResource().assertThat(hasLink(FIRST_ORDER));
	}

	@ParameterizedTest
	@MethodSource("packsResponse")
	void testEachPackHasFirstOrderLink(Response response) {
		response.getResource().asCollection()//
				.stream().forEach(item -> {
					// Not expected in packs collection
					// Check in pack directly
					Resource pack = item.callSelfLink().getResource();
					pack.assertThat(hasLink(FIRST_ORDER));
				});
	}

	@ParameterizedTest
	@MethodSource("firstOrderPacksResponse")
	void testFirstOrderPacksReturnsNextOrdersLink(Response response) {
		response.getResource().assertThat(hasLink(NEXT_ORDERS));
	}

	@ParameterizedTest
	@MethodSource("firstOrderPacksResponse")
	void testEachFirstOrderPackHasFirstOrderLink(Response response) {
		response.getResource().asCollection()//
				.stream().forEach(item -> {
					// Not expected in packs collection
					// Check in pack directly
					Resource pack = item.callSelfLink().getResource();
					pack.assertThat(hasLink(NEXT_ORDERS));
				});
	}

	@ParameterizedTest
	@MethodSource("packsAndFirstOrderPacksResponses")
	void testPackCollectionHasSelfLink(Response response) {
		response.getResource().assertThat(hasSelfLink());
	}

	@ParameterizedTest
	@MethodSource("packsAndFirstOrderPacksResponses")
	void testEachPackHasSelfLink(Response response) {
		response.getResource().asCollection()//
				.stream().forEach(item -> {
					// Check in packs collection
					item.assertThat(hasSelfLink());

					// Check in pack directly
					Resource pack = item.callSelfLink().getResource();
					pack.assertThat(hasSelfLink());
				});
	}

	@ParameterizedTest
	@MethodSource("packsAndFirstOrderPacksResponses")
	void testEachPackHasPacksLink(Response response) {
		response.getResource().asCollection()//
				.stream().forEach(item -> {
					// Not expected in packs collection

					// Check in pack directly
					Resource pack = item.callSelfLink().getResource();
					pack.assertThat(hasLink(PACKS));
				});
	}

	@ParameterizedTest
	@MethodSource("packsAndFirstOrderPacksResponses")
	void testEachPackHasPriceInEuros(Response response) {
		response.getResource().asCollection()//
				.stream().forEach(item -> {
					// Check in packs collection
					item.assertThat(hasJsonItem(EUROS));
					item.assertThat(jsonPath(EUROS), isA(Float.class));

					// Check in pack directly
					Resource pack = item.callSelfLink().getResource();
					pack.assertThat(hasJsonItem(EUROS));
					pack.assertThat(jsonPath(EUROS), isA(Float.class));
				});
	}

	@ParameterizedTest
	@MethodSource("packsAndFirstOrderPacksResponses")
	void testPacksHaveMihoyoPrices(Response response) {
		CollectionResource resource = response.getResource().asCollection();
		resource.getItem(0).assertThat(jsonPath(EUROS), equalTo(1.09f));
		resource.getItem(1).assertThat(jsonPath(EUROS), equalTo(5.49f));
		resource.getItem(2).assertThat(jsonPath(EUROS), equalTo(16.99f));
		resource.getItem(3).assertThat(jsonPath(EUROS), equalTo(32.99f));
		resource.getItem(4).assertThat(jsonPath(EUROS), equalTo(54.99f));
		resource.getItem(5).assertThat(jsonPath(EUROS), equalTo(109.99f));
	}

	@ParameterizedTest
	@MethodSource("packsAndFirstOrderPacksResponses")
	void testEachPackHasCristals(Response response) {
		response.getResource().asCollection()//
				.stream().forEach(item -> {
					// Check in packs collection
					item.assertThat(hasJsonItem(CRISTALS));
					item.assertThat(jsonPath(CRISTALS), isA(Integer.class));

					// Check in pack directly
					Resource pack = item.callSelfLink().getResource();
					pack.assertThat(hasJsonItem(CRISTALS));
					pack.assertThat(jsonPath(CRISTALS), isA(Integer.class));
				});
	}

	@Test
	void testFirstOrderPacksHaveTwiceCristals() {
		SERVICE.callRoot().callResourceLink(PACKS)//
				.getResource().asCollection()//
				.stream()//
				.map(Resource::callSelfLink)// Retrieve all info, not just what is exposed in collection
				.map(Response::getResource)//
				.forEach(defaultPack -> {
					Resource firstOrderPack = defaultPack.callLink(FIRST_ORDER).getResource();
					firstOrderPack.assertThat(//
							jsonPath(CRISTALS), //
							equalTo(2 * defaultPack.<Integer>get(CRISTALS)));
				});
	}

	static Stream<Response> packsResponse() {
		return Stream.of(SERVICE.callRoot().callResourceLink(PACKS));
	}

	static Stream<Response> firstOrderPacksResponse() {
		return Stream.of(SERVICE.callRoot().callResourceLink(PACKS).callResourceLink(FIRST_ORDER));
	}

	static Stream<Response> packsAndFirstOrderPacksResponses() {
		return Stream.concat(packsResponse(), firstOrderPacksResponse());
	}
}
