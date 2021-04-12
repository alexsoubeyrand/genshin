package fr.sazaju.genshin.service.hateoas;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CollectionResource extends Resource {

	public CollectionResource(String url, io.restassured.response.Response response, JsonPath jsonPath) {
		super(url, response, jsonPath);
	}

	public Stream<Resource> stream() {
		int size = response.body().jsonPath().get("_embedded.packList.size()");
		return IntStream.range(0, size)//
				.mapToObj(this::itemPath)//
				.map(itemPath -> new Resource(url, response, itemPath));
	}

	public Resource getItem(int i) {
		return new Resource(url, response, itemPath(i));
	}

	private JsonPath itemPath(int i) {
		return jsonPath.append("_embedded.packList[" + i + "]");
	}

}
