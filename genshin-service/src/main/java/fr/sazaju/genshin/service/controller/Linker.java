package fr.sazaju.genshin.service.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;

import fr.sazaju.genshin.model.Pack;
import fr.sazaju.genshin.service.Rel;
import fr.sazaju.genshin.simulator.wish.Profile;
import fr.sazaju.genshin.simulator.wish.Settings;
import fr.sazaju.genshin.simulator.wish.Wish;

public class Linker {
	private final Predicate<LinkRelation> relationFilter;

	private Linker(Predicate<LinkRelation> relationFilter) {
		this.relationFilter = relationFilter;
	}

	public static Linker allLinks() {
		return new Linker(relation -> true);
	}

	public static Linker selfLink() {
		return new Linker(Predicate.isEqual(Rel.Iana.SELF));
	}

	public EntityModel<Pack> decoratePack(EntityModel<Pack> model) {
		Pack pack = model.getContent();
		String euros = "" + pack.euros;
		return addFilteredLinks(model, Map.of(//
				Rel.Iana.SELF, () -> methodOn(PackController.class).getPackByEuros(euros), //
				Rel.Packs.FIRST_ORDER, () -> methodOn(PackController.class).getFirstOrderPackByEuros(euros), //
				Rel.Packs.PACKS, () -> methodOn(PackController.class).getPacks()//
		));
	}

	public EntityModel<Pack> decorateFirstOrderPack(EntityModel<Pack> model) {
		Pack pack = model.getContent();
		String euros = "" + pack.euros;
		return addFilteredLinks(model, Map.of(//
				Rel.Iana.SELF, () -> methodOn(PackController.class).getFirstOrderPackByEuros(euros), //
				Rel.Packs.NEXT_ORDERS, () -> methodOn(PackController.class).getPackByEuros(euros), //
				Rel.Packs.PACKS, () -> methodOn(PackController.class).getFirstOrderPacks()//
		));
	}

	public CollectionModel<EntityModel<Pack>> decoratePackCollection(CollectionModel<EntityModel<Pack>> model) {
		return addFilteredLinks(model, Map.of(//
				Rel.Iana.SELF, () -> methodOn(PackController.class).getPacks(), //
				Rel.Packs.FIRST_ORDER, () -> methodOn(PackController.class).getFirstOrderPacks() //
		));
	}

	public CollectionModel<EntityModel<Pack>> decorateFirstOrderPackCollection(
			CollectionModel<EntityModel<Pack>> model) {
		return addFilteredLinks(model, Map.of(//
				Rel.Iana.SELF, () -> methodOn(PackController.class).getFirstOrderPacks(), //
				Rel.Packs.NEXT_ORDERS, () -> methodOn(PackController.class).getPacks() //
		));
	}

	public <T extends RepresentationModel<?>> T decorateBannerCollection(T model) {
		return addFilteredLinks(model, Map.of(//
				Rel.Iana.SELF, () -> methodOn(BannerController.class).getBanners(), //
				Rel.Banners.CHARACTERS, () -> methodOn(BannerController.class).getCharactersBanner() //
		));
	}

	public <T extends RepresentationModel<?>> T decorateCharactersBanner(T model) {
		return addFilteredLinks(model, Map.of(//
				Rel.Iana.SELF, () -> methodOn(BannerController.class).getCharactersBanner(), //
				Rel.Banners.WISH, () -> methodOn(BannerController.class).getCharactersBannerWish() //
		));
	}

	public EntityModel<Wish> decorateCharactersBannerWish(//
			EntityModel<Wish> model, //
			Function<Settings, String> settingsSerializer, Settings settings, //
			Function<Profile, String> profileSerializer, Profile profileStart, Profile profileEnd) {
		return addFilteredLinks(model, Map.of(//
				Rel.Iana.SELF, () -> methodOn(BannerController.class).getCharactersBannerWish(), //
				Rel.Banners.SETTINGS,
				() -> methodOn(BannerController.class)
						.getCharactersBannerWishSettings(settingsSerializer.apply(settings)), //
				Rel.Banners.PROFILE_START,
				() -> methodOn(BannerController.class)
						.getCharactersBannerWishProfile(profileSerializer.apply(profileStart)), //
				Rel.Banners.PROFILE_END, () -> methodOn(BannerController.class)
						.getCharactersBannerWishProfile(profileSerializer.apply(profileEnd)) //
		));
	}

	public <T extends RepresentationModel<?>> T decorateCharactersBannerWishSettings(T model, String serial) {
		return addFilteredLinks(model, Map.of(//
				Rel.Iana.SELF, () -> methodOn(BannerController.class).getCharactersBannerWishSettings(serial) //
		));
	}

	public EntityModel<Profile> decorateCharactersBannerWishProfile(EntityModel<Profile> model, String serial) {
		return addFilteredLinks(model, Map.of(//
				Rel.Iana.SELF, () -> methodOn(BannerController.class).getCharactersBannerWishProfile(serial) //
		));
	}

	private <T extends RepresentationModel<?>> T addFilteredLinks(T model, Map<LinkRelation, Supplier<?>> relations) {
		relations.forEach((relation, resourceSupplier) -> {
			if (relationFilter.test(relation)) {
				model.add(linkTo(resourceSupplier.get()).withRel(relation));
			}
		});
		return model;
	}

}
