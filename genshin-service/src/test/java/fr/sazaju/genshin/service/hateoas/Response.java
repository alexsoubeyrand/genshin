package fr.sazaju.genshin.service.hateoas;

public class Response {

	private final String url;
	private final io.restassured.response.Response response;

	Response(String url, io.restassured.response.Response response) {
		this.url = url;
		this.response = response;
	}

	public Response callResourceLink(String rel) {
		return getResource().callLink(rel);
	}

	public Resource getResource() {
		return new Resource(url, response, new JsonPath());
	}

	@Override
	public String toString() {
		return "Response from " + url;
	}
}
