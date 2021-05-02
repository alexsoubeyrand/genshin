package fr.sazaju.genshin.service.model;

import org.springframework.hateoas.RepresentationModel;

public class ConfPatch extends RepresentationModel<ConfPatch> {
	public String test;
	public Float fixedRandom;
	public int consecutiveWishesBelow4Stars;
}
