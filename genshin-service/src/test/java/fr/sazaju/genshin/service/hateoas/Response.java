package fr.sazaju.genshin.service.hateoas;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.sazaju.genshin.service.hateoas.Href.Method;

public class Response {

	private final String url;
	private final io.restassured.response.Response response;

	Response(String url, io.restassured.response.Response response) {
		this.url = url;
		this.response = response;
	}

	public Response callResourceLink(String rel) {
		return callResourceLink(rel, Method.GET);
	}

	public Response callResourceLink(String rel, Method method) {
		return getResource().callLink(rel, method);
	}

	public Response callResourceLink(String rel, Method method, Object body) throws JsonProcessingException {
		return getResource().callLink(rel, method, body);
	}

	public Response callResourceSelfLink() {
		return callResourceSelfLink(Method.GET);
	}

	public Response callResourceSelfLink(Method method) {
		return getResource().callSelfLink(method);
	}

	public Response callResourceSelfLink(Method method, Object body) throws JsonProcessingException {
		return getResource().callSelfLink(method, body);
	}

	public Resource getResource() {
		return new Resource(url, response, new JsonPath());
	}

	@Override
	public String toString() {
		return "Response from " + url;
	}
}
