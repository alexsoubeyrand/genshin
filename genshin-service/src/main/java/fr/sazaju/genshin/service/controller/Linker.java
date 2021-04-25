package fr.sazaju.genshin.service.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.HashMap;
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
import fr.sazaju.genshin.service.controller.coder.SimulatorDefinition.Simulator;
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

	public <T extends RepresentationModel<?>> T decorateCharactersBanner(T model) {
		return addFilteredLinks(model, Map.of(//
				Rel.Iana.SELF, () -> methodOn(CharactersBannerController.class).getCharactersBanner(), //
				Rel.Banners.WISHES, () -> methodOn(CharactersBannerController.class).getCharactersBannerWishes() //
		));
	}

	public CollectionModel<Wish> decorateCharactersBannerWishes(//
			CollectionModel<Wish> model, //
			Function<Simulator, String> simulatorSerializer, Simulator simulator, Simulator nextSimulator, //
			Function<Settings, String> settingsSerializer, Function<Profile, String> profileSerializer) {
		Map<LinkRelation, Supplier<?>> relations = new HashMap<>();
		relations.put(Rel.Iana.SELF, () -> methodOn(CharactersBannerController.class)
				.getCharactersBannerWishes(simulatorSerializer.apply(simulator)));
		relations.put(Rel.Banners.SETTINGS, () -> methodOn(CharactersBannerController.class)
				.getCharactersBannerWishSettings(settingsSerializer.apply(simulator.settings)));
		relations.put(Rel.Banners.PROFILE_START, () -> methodOn(CharactersBannerController.class)
				.getCharactersBannerWishProfile(profileSerializer.apply(simulator.profile)));
		relations.put(Rel.Banners.PROFILE_END, () -> methodOn(CharactersBannerController.class)
				.getCharactersBannerWishProfile(profileSerializer.apply(nextSimulator.profile)));

		if (nextSimulator.numberGeneratorDescriptor.wishesCount > 0) {
			relations.put(Rel.Iana.NEXT, () -> methodOn(CharactersBannerController.class)
					.getCharactersBannerWishes(simulatorSerializer.apply(nextSimulator)));
		}

		return addFilteredLinks(model, relations);
	}

	public EntityModel<Settings> decorateCharactersBannerWishSettings(EntityModel<Settings> model,
			Function<Settings, String> serializer, Settings mihoyoSettings) {
		Settings settings = model.getContent();
		return addFilteredLinks(model, Map.of(//
				Rel.Iana.SELF,
				() -> methodOn(CharactersBannerController.class)
						.getCharactersBannerWishSettings(serializer.apply(settings)), //
				Rel.Global.MIHOYO, () -> methodOn(CharactersBannerController.class)
						.getCharactersBannerWishSettings(serializer.apply(mihoyoSettings)) //
		));
	}

	public EntityModel<Profile> decorateCharactersBannerWishProfile(EntityModel<Profile> model,
			Function<Profile, String> serializer, Profile defautProfile) {
		Profile profile = model.getContent();
		return addFilteredLinks(model, Map.of(//
				Rel.Iana.SELF,
				() -> methodOn(CharactersBannerController.class)
						.getCharactersBannerWishProfile(serializer.apply(profile)), //
				Rel.Global.DEFAULT, () -> methodOn(CharactersBannerController.class)
						.getCharactersBannerWishProfile(serializer.apply(defautProfile)) //
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
