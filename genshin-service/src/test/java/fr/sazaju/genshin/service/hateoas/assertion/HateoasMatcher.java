package fr.sazaju.genshin.service.hateoas.assertion;

import java.util.function.Function;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.sazaju.genshin.service.hateoas.Resource;

public abstract class HateoasMatcher<T> extends BaseMatcher<T> {

	public abstract HateoasMatcher<T> forResource(Resource resource);

	public static <T> HateoasMatcher<T> hasJsonItem(String jsonPath) {
		return new HateoasMatcher<T>() {

			@Override
			public boolean matches(Object actual) {
				String actualString;
				if (actual instanceof String) {
					// TODO Old way to go, assume we get the proper object
					actualString = (String) actual;
				} else {
					try {
						actualString = new ObjectMapper().writeValueAsString(actual);
					} catch (JsonProcessingException cause) {
						throw new RuntimeException(cause);
					}
				}
				return io.restassured.path.json.JsonPath.from(actualString).get(jsonPath) != null;
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
	
	public static <T> HateoasMatcher<T> jsonEqualTo(Function<T, T> contentAdapter, T expected) {
		return new HateoasMatcher<T>() {

			Matcher<T> matcher;
			
			@Override
			public boolean matches(Object actual) {
				String actualString;
				if (actual instanceof String) {
					// TODO Old way to go, assume we get the proper object
					actualString = (String) actual;
				} else {
					try {
						actualString = new ObjectMapper().writeValueAsString(actual);
					} catch (JsonProcessingException cause) {
						throw new RuntimeException(cause);
					}
				}
				T objectActual = io.restassured.path.json.JsonPath.from(actualString).get();
				T adaptedActual = contentAdapter.apply(objectActual);
				matcher = Matchers.equalTo(expected);
				return matcher.matches(adaptedActual);
			}

			@Override
			public void describeTo(Description description) {
				if (matcher == null) {
					description.appendText("<no description yet>");// TODO
				} else {
					matcher.describeTo(description);
				}
			}

			@Override
			public HateoasMatcher<T> forResource(Resource resource) {
				return this;// TODO
			}
		};
	}

	public static <T> HateoasMatcher<T> hasLink(String rel) {
		return hasJsonItem("_links.'" + rel + "'");
	}

	public static <T> HateoasMatcher<T> hasSelfLink() {
		return hasLink("self");
	}
}
