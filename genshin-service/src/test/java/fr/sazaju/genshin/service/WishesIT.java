package fr.sazaju.genshin.service;

import static fr.sazaju.genshin.service.Links.Banners.*;
import static fr.sazaju.genshin.service.WishesIT.Property.*;
import static fr.sazaju.genshin.service.hateoas.assertion.HateoasMatcher.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.service.hateoas.HateoasClient;
import fr.sazaju.genshin.service.hateoas.Resource;
import fr.sazaju.genshin.service.hateoas.assertion.ResourceExtractor;

class WishesIT {

	// TODO test permanent & weapon banners
	// TODO test self link everywhere

	private static final String WISH = "wish";
	private static final String CHARACTERS = "characters";
	private static final String PROFILES = "profiles";
	private static final String MIHOYO = "mihoyo";
	private static final HateoasClient SERVICE = new HateoasClient();

	@Test
	void testRootHasBannersLink() {
		SERVICE.callRoot()//
				.getResource()//
				.assertThat(hasLink(BANNERS));
	}

	@Test
	void testBannersHaveCharacterEventLink() {
		SERVICE.callRoot()//
				.callResourceLink(BANNERS)//
				.getResource()//
				.assertThat(hasLink(CHARACTERS));
	}

	@Test
	void testCharacterBannerHasProfilesLink() {
		SERVICE.callRoot()//
				.callResourceLink(BANNERS)//
				.callResourceLink(CHARACTERS)//
				.getResource()//
				.assertThat(hasLink(PROFILES));
	}

	@Test
	void testCharacterBannerProfilesHaveMihoyoLink() {
		SERVICE.callRoot()//
				.callResourceLink(BANNERS)//
				.callResourceLink(CHARACTERS)//
				.callResourceLink(PROFILES)//
				.getResource()//
				.assertThat(hasLink(MIHOYO));
	}

	@ParameterizedTest
	@MethodSource("allProperties")
	void testCharacterBannerWithMihoyoProfileHasExpectedProperty(String property) {
		SERVICE.callRoot()//
				.callResourceLink(BANNERS)//
				.callResourceLink(CHARACTERS)//
				.callResourceLink(PROFILES)//
				.callResourceLink(MIHOYO)//
				.getResource()//
				.assertThat(hasItem(property));
	}

	@Test
	void testCharacterBannerWithMihoyoProfileHaveMihoyoProperties() {
		Resource profile = SERVICE.callRoot()//
				.callResourceLink(BANNERS)//
				.callResourceLink(CHARACTERS)//
				.callResourceLink(PROFILES)//
				.callResourceLink(MIHOYO)//
				.getResource();

		profile.assertThat(jsonPath(RATE_3_STARS), equalTo(0.943f));
		profile.assertThat(jsonPath(RATE_4_STARS), equalTo(0.051f));
		profile.assertThat(jsonPath(RATE_4_STARS_WEAPON_CHARCTER), equalTo(0.5f));
		profile.assertThat(jsonPath(RATE_5_STARS), equalTo(0.006f));
		profile.assertThat(jsonPath(RATE_5_STARS_PERMANENT_EXCLUSIVE), equalTo(0.5f));
		profile.assertThat(jsonPath(PITY_4_STARS), equalTo(10));
		profile.assertThat(jsonPath(PITY_5_STARS), equalTo(90));
	}

	@Test
	void testCharacterBannerWithMihoyoProfileHasWishLink() {
		SERVICE.callRoot()//
				.callResourceLink(BANNERS)//
				.callResourceLink(CHARACTERS)//
				.callResourceLink(PROFILES)//
				.callResourceLink(MIHOYO)//
				.getResource()//
				.assertThat(hasLink(WISH));
	}

	// XXX /wishes/{type}/profiles/{profile}/wish
	// type := permanent | weaponEvent | characterEvent
	// profile := data/{data} | keys/{profileKey}

	@Disabled
	@Test
	void testCharacterWishWithMihoyoProfileHasCorrectResult() {
		// TODO Parameterize
		double random = 1.0;

		// TODO Parameterize
		int counterBelow4Stars = 0;
		int counterBelow5Stars = 0;
		boolean guarantyExclusiveOnNext5Stars = false;

		int expectedStars = 3;
		String expectedType = "weapon";
		boolean expectedExclusive = false;

		Resource wish = SERVICE.callRoot()//
				.callResourceLink(BANNERS)//
				.callResourceLink(CHARACTERS)//
				.callResourceLink(PROFILES)//
				.callResourceLink(MIHOYO)//
				.getResource().getLink(WISH).href().call()// TODO Manage args
				.getResource();

		wish.assertThat(jsonPath(STARS), equalTo(expectedStars));
		wish.assertThat(jsonPath(TYPE), equalTo(expectedType));
		wish.assertThat(jsonPath(EXCLUSIVE), equalTo(expectedExclusive));
	}

	// TODO Test series of wishes

	static enum Property {
		// Profile
		RATE_3_STARS("rate3Stars"), //
		RATE_4_STARS("rate4Stars"), //
		RATE_4_STARS_WEAPON_CHARCTER("rate4StarsWeaponCharcter"), //
		RATE_5_STARS("rate5Stars"), //
		RATE_5_STARS_PERMANENT_EXCLUSIVE("rate5StarsPermanentExclusive"), //
		PITY_4_STARS("pity4Stars"), //
		PITY_5_STARS("pity5Stars"), //

		// Wish
		STARS("stars"), //
		TYPE("type"), //
		EXCLUSIVE("exclusive"),//
		;

		private final String name;

		Property(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	static Stream<Property> allProperties() {
		return Stream.of(Property.values());
	}

	private <T> ResourceExtractor<T> jsonPath(Property property) {
		return ResourceExtractor.jsonPath(property.toString());
	}
}
