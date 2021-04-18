package fr.sazaju.genshin.service.hateoas;

public class Link {

	private final io.restassured.response.Response response;
	private final JsonPath jsonPath;

	Link(io.restassured.response.Response response, JsonPath jsonPath, String rel) {
		this.response = response;
		// TODO Support multiple rels: https://tools.ietf.org/html/rfc8288#section-3.3
		this.jsonPath = jsonPath.append("_links").append(rel);
	}

	public Href href() {
		return new Href(response, jsonPath);
	}

}
