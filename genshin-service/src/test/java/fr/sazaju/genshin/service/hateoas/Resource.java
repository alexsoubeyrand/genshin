package fr.sazaju.genshin.service.hateoas;

import org.hamcrest.Matcher;

import fr.sazaju.genshin.service.hateoas.assertion.HateoasMatcher;
import fr.sazaju.genshin.service.hateoas.assertion.ResourceExtractor;

public class Resource {

	protected final String url;
	protected final io.restassured.response.Response response;
	protected final JsonPath jsonPath;

	public Resource(String url, io.restassured.response.Response response, JsonPath jsonPath) {
		this.url = url;
		this.response = response;
		this.jsonPath = jsonPath;
	}

	public Link getLink(String rel) {
		return new Link(response, jsonPath, rel);
	}

	public Resource extract(String jsonPath) {
		return new Resource(url, response, this.jsonPath.append(jsonPath));
	}

	public <T> T resolve() {
		return response.body().jsonPath().get(jsonPath.toString());
	}

	public <T> T get(String jsonPath) {
		return extract(jsonPath).resolve();
	}

	public Response callLink(String rel) {
		return getLink(rel).href().call();
	}

	public Response callSelfLink() {
		return callLink("self");
	}

	public CollectionResource asCollection() {
		return new CollectionResource(url, response, jsonPath);
	}

	public JsonPath getJsonPath() {
		return jsonPath;
	}

	public <T> void assertThat(ResourceExtractor<T> extractor, Matcher<T> matcher) {
		response.then().body(jsonPath.append(extractor.getRelativeJsonPath()).toString(), matcher);
	}

	public <T> void assertThat(Matcher<T> matcher) {
		response.then().body(matcher);
	}

	public <T> void assertThat(ResourceExtractor<T> extractor, HateoasMatcher<T> matcher) {
		assertThat(extractor, (Matcher<T>) matcher.forResource(this));
	}

	public <T> void assertThat(HateoasMatcher<T> matcher) {
		assertThat((Matcher<T>) matcher.forResource(this));
	}

	@Override
	public String toString() {
		if (jsonPath.isRoot()) {
			return "Resource from " + url;
		} else {
			return "Resource " + jsonPath + " from " + url;
		}
	}
}
