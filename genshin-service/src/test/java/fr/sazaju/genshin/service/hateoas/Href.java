package fr.sazaju.genshin.service.hateoas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Href {

	private final io.restassured.response.Response response;
	private final JsonPath jsonPath;
	private final IllegalArgumentException illegalArgumentException;

	Href(io.restassured.response.Response response, JsonPath jsonPath) {
		this.response = response;
		this.jsonPath = jsonPath.append("href");
		this.illegalArgumentException = new IllegalArgumentException(String.format("No item %s found", jsonPath));
	}

	public Response call(Method method) {
		return callIntern(method);
	}

	public Response call(Method method, Object body) throws JsonProcessingException {
		try {
			return callIntern(method, spec -> {
				try {
					return method.prepareWithBody(spec, body);
				} catch (JsonProcessingException cause) {
					throw new WrapException(cause);
				}
			});
		} catch (WrapException cause) {
			throw cause.getJsonCause();
		}
	}

	private Response callIntern(Method method, Step... steps) {
		String url = response.then().extract().path(jsonPath.toString());
		if (url == null) {
			throw illegalArgumentException;
		}
		io.restassured.specification.RequestSpecification spec = io.restassured.RestAssured.given();
		for (Step step : steps) {
			spec = step.execute(spec);
		}
		io.restassured.response.Response newResponse = method.execute(spec, url);
		return new Response(url, newResponse);
	}

	interface Step {
		io.restassured.specification.RequestSpecification execute(
				io.restassured.specification.RequestSpecification specification);
	}

	public Response get() {
		return call(Method.GET);
	}

	public Response patch(Object body) throws JsonProcessingException {
		return call(Method.PATCH, body);
	}

	private static String jsonOf(Object body) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(body);
	}

	public enum Method {
		GET(//
				(spec, body) -> spec, //
				(spec, url) -> spec.get(url)//
		), PATCH(//
				(spec, body) -> spec//
						.header("Content-type", "application/json")//
						.body(jsonOf(body)), //
				(spec, url) -> spec.patch(url)//
		);

		interface PreparerWithBody {
			io.restassured.specification.RequestSpecification apply(
					io.restassured.specification.RequestSpecification specification, Object body)
					throws JsonProcessingException;
		}

		interface Executor {
			io.restassured.response.Response execute(io.restassured.specification.RequestSpecification specification,
					String url);
		}

		private final PreparerWithBody preparerWithBody;
		private final Executor executor;

		private Method(PreparerWithBody preparerWithBody, Executor executor) {
			this.preparerWithBody = preparerWithBody;
			this.executor = executor;
		}

		io.restassured.specification.RequestSpecification prepareWithBody(
				io.restassured.specification.RequestSpecification specification, Object body)
				throws JsonProcessingException {
			return preparerWithBody.apply(specification, body);
		}

		io.restassured.response.Response execute(io.restassured.specification.RequestSpecification specification,
				String url) {
			return executor.execute(specification, url);
		}
	}

	@SuppressWarnings("serial")
	private class WrapException extends RuntimeException {
		private final JsonProcessingException cause;

		public WrapException(JsonProcessingException cause) {
			this.cause = cause;
		}

		public JsonProcessingException getJsonCause() {
			return cause;
		}
	}
}
