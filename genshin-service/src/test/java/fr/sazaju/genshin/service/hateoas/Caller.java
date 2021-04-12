package fr.sazaju.genshin.service.hateoas;

public class Caller {

	private final String url;

	Caller(String url) {
		this.url = url;
	}

	public Response call() {
		io.restassured.response.Response response = io.restassured.RestAssured.get(url);
		return new Response(url, response);
	}

}
