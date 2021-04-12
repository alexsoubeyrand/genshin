package fr.sazaju.genshin.service.hateoas.assertion;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import fr.sazaju.genshin.service.hateoas.Resource;

public abstract class HateoasMatcher<T> extends BaseMatcher<T> {

	public abstract HateoasMatcher<T> forResource(Resource resource);

	public static <T> HateoasMatcher<T> hasJsonItem(String jsonPath) {
		return new HateoasMatcher<T>() {

			@Override
			public boolean matches(Object actual) {
				return io.restassured.path.json.JsonPath.from((String) actual).get(jsonPath) != null;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("has item " + jsonPath);
			}

			@Override
			public HateoasMatcher<T> forResource(Resource resource) {
				return hasJsonItem(resource.getJsonPath().append(jsonPath).toString());
			}
		};
	}

	public static <T> HateoasMatcher<T> hasLink(String rel) {
		return hasJsonItem("_links." + rel);
	}

	public static <T> HateoasMatcher<T> hasSelfLink() {
		return hasLink("self");
	}
}
