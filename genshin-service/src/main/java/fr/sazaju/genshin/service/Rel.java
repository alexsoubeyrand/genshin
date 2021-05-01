package fr.sazaju.genshin.service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.function.Function;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.LinkRelation;

import fr.sazaju.genshin.service.controller.RelsController;

public class Rel {
	public static class Iana {
		public static final LinkRelation SELF = IanaLinkRelations.SELF;
		public static final LinkRelation NEXT = IanaLinkRelations.NEXT;
		public static final LinkRelation ABOUT = IanaLinkRelations.ABOUT;
		public static final LinkRelation AUTHOR = IanaLinkRelations.AUTHOR;
	}

	public static class Global {
		public static final LinkRelation SOURCE = customRelation(RelsController::source);
		public static final LinkRelation DEFAULT = customRelation(RelsController::defaultt);
		public static final LinkRelation MIHOYO = customRelation(RelsController::mihoyo);
	}

	public static class Packs {
		public static final LinkRelation PACKS = customRelation(RelsController::packs);
		public static final LinkRelation FIRST_ORDER = customRelation(RelsController::firstOrder);
		public static final LinkRelation NEXT_ORDERS = customRelation(RelsController::nextOrders);
	}

	public static class Banners {
		public static final LinkRelation CHARACTER_BANNER = customRelation(RelsController::characterBanner);
		public static final LinkRelation NEXT_RUN = customRelation(RelsController::nextRun);
		public static final LinkRelation NEXT_MULTI = customRelation(RelsController::nextMulti);
		public static final LinkRelation WISH = customRelation(RelsController::wish);
		public static final LinkRelation WISHES = customRelation(RelsController::wishes);
		public static final LinkRelation SETTINGS = customRelation(RelsController::settings);
		public static final LinkRelation PROFILE = customRelation(RelsController::profile);
		public static final LinkRelation PROFILE_START = customRelation(RelsController::profileStart);
		public static final LinkRelation PROFILE_END = customRelation(RelsController::profileEnd);
		public static final LinkRelation CONFIGURATION = customRelation(RelsController::configuration);
		public static final LinkRelation NEXT_CONFIGURATION = customRelation(RelsController::nextConfiguration);
	}

	private static LinkRelation customRelation(Function<RelsController, ?> relMethod) {
		return LinkRelation.of(linkTo(relMethod.apply(methodOn(RelsController.class))).toUri().toString());
	}
}
