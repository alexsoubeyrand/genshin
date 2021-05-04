package fr.sazaju.genshin.service.hateoas.assertion;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface ResourceExtractor<T> {
	String getRelativeJsonPath();

	T adapt(T object);

	public static <T> ResourceExtractor<T> jsonPath(String jsonPath) {
		return new ResourceExtractor<T>() {
			@Override
			public String getRelativeJsonPath() {
				return jsonPath;
			}

			@Override
			public T adapt(T object) {
				return object;
			}
		};
	}

	public static <T> ResourceExtractor<T> adapted(Function<T, T> contentAdapter) {
		return new ResourceExtractor<T>() {
			@Override
			public String getRelativeJsonPath() {
				return "";
			}

			@Override
			public T adapt(T object) {
				return contentAdapter.apply(object);
			}
		};
	}

	public static ResourceExtractor<Map<String, Object>> ignoringFields(List<String> ignoredFields) {
		return adapted(body -> {
			ignoredFields.forEach(field -> body.remove(field));
			return body;
		});
	}

	public static ResourceExtractor<Map<String, Object>> ignoringHalFormFields() {
		return ignoringFields(List.of("_links", "_templates"));
	}

	static <T> ResourceExtractor<T> link(String rel) {
		return jsonPath("_links." + rel);
	}

	static <T> ResourceExtractor<T> selfLink() {
		return link("self");
	}

	static <T> ResourceExtractor<T> linkHref(String rel) {
		return jsonPath("_links." + rel + ".href");
	}

}
