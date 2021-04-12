package fr.sazaju.genshin.service.hateoas.assertion;

public interface ResourceExtractor<T> {
	String getRelativeJsonPath();

	public static <T> ResourceExtractor<T> jsonPath(String jsonPath) {
		return () -> jsonPath;
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
