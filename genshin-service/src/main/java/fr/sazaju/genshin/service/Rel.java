package fr.sazaju.genshin.service;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.LinkRelation;

public class Rel {
	// IANA
	public static final LinkRelation SELF = IanaLinkRelations.SELF;
	public static final LinkRelation ABOUT = IanaLinkRelations.ABOUT;
	public static final LinkRelation AUTHOR = IanaLinkRelations.AUTHOR;

	// Generic
	public static final LinkRelation SOURCE = LinkRelation.of("source");

	// Genshin-related
	public static final LinkRelation PACKS = LinkRelation.of("packs");
	public static final LinkRelation FIRST_ORDER = LinkRelation.of("firstOrder");
	public static final LinkRelation NEXT_ORDERS = LinkRelation.of("nextOrders");

}
