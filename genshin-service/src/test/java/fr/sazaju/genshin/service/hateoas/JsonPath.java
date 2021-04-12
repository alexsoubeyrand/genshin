package fr.sazaju.genshin.service.hateoas;

public class JsonPath {

	private final String path;

	public JsonPath(String path) {
		this.path = path;
	}

	public JsonPath() {
		this.path = "";
	}

	public JsonPath append(String subPath) {
		if (path.isEmpty()) {
			return new JsonPath(subPath);
		} else {
			return new JsonPath(path + "." + subPath);
		}
	}

	public boolean isRoot() {
		return path.isEmpty();
	}

	@Override
	public String toString() {
		return path;
	}

}
