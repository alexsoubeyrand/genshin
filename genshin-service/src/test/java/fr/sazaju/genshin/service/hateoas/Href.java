package fr.sazaju.genshin.service.hateoas;

public class Href {

	private final io.restassured.response.Response response;
	private final JsonPath jsonPath;
	private final IllegalArgumentException illegalArgumentException;

	Href(io.restassured.response.Response response, JsonPath jsonPath) {
		this.response = response;
		this.jsonPath = jsonPath.append("href");
		this.illegalArgumentException = new IllegalArgumentException(String.format("No item %s found", jsonPath));
	}

	public Response call() {
		String url = response.then().extract().path(jsonPath.toString());
		if (url == null) {
			throw illegalArgumentException;
		}
		io.restassured.response.Response newResponse = io.restassured.RestAssured.get(url);
		return new Response(url, newResponse);
	}

}
