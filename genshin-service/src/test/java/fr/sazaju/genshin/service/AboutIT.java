package fr.sazaju.genshin.service;

import static fr.sazaju.genshin.service.Links.About.*;
import static fr.sazaju.genshin.service.hateoas.assertion.HateoasMatcher.*;
import static fr.sazaju.genshin.service.hateoas.assertion.ResourceExtractor.*;
import static org.hamcrest.text.MatchesPattern.*;

import org.junit.jupiter.api.Test;

import fr.sazaju.genshin.service.hateoas.HateoasClient;

class AboutIT {

	private static final HateoasClient SERVICE = new HateoasClient();

	@Test
	void testRootReturnsAboutLink() {
		SERVICE.callRoot()//
				.getResource()//
				.assertThat(hasLink(ABOUT));
	}

	@Test
	void testAboutReturnsSelfLink() {
		SERVICE.callRoot()//
				.callResourceLink(ABOUT)//
				.getResource()//
				.assertThat(hasSelfLink());
	}

	@Test
	void testAboutReturnsAuthorLink() {
		SERVICE.callRoot()//
				.callResourceLink(ABOUT)//
				.getResource()//
				.assertThat(hasLink(AUTHOR));
	}

	@Test
	void testAboutReturnsAuthorLinkAsEmail() {
		SERVICE.callRoot()//
				.callResourceLink(ABOUT)//
				.getResource()//
				.assertThat(linkHref(AUTHOR), matchesPattern("mailto:.*"));
	}

	@Test
	void testAboutReturnsSourceLink() {
		SERVICE.callRoot()//
				.callResourceLink(ABOUT)//
				.getResource()//
				.assertThat(hasLink(SOURCE));
	}

}
