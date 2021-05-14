package fr.sazaju.genshin.service.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import fr.sazaju.genshin.banner.character.Wish;
import fr.sazaju.genshin.model.Pack;
import fr.sazaju.genshin.service.Rel;
import fr.sazaju.genshin.service.controller.coder.ConfigurationDefinition.Configuration;

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
				Rel.Iana.SELF, () -> methodOn(CharacterBannerController.class).getBanner(), //
				Rel.Banners.CONFIGURATION, () -> methodOn(CharacterBannerController.class).getConfiguration() //
		));
	}

	public EntityModel<Configuration> decorateCharactersBannerWishConfiguration(EntityModel<Configuration> model,
			Function<Configuration, String> serializer) {
		Configuration configuration = model.getContent();
		CharacterBannerController controller = methodOn(CharacterBannerController.class);
		return addFilteredLinks(model, //
				Map.of(//
						Rel.Iana.SELF, () -> controller.getConfiguration(serializer.apply(configuration)), //
						Rel.Banners.NEXT_RUN, () -> controller.getRun(serializer.apply(configuration)), //
						Rel.Banners.NEXT_MULTI, () -> controller.getMulti(serializer.apply(configuration)), //
						Rel.Banners.STATS, () -> controller.getStats(serializer.apply(configuration))//
				), //
				List.of(//
						() -> controller.patchConfiguration(serializer.apply(configuration), null)//
				)//
		);
	}

	public EntityModel<Wish> decorateCharactersBannerWish(//
			EntityModel<Wish> model, //
			Function<Configuration, String> configurationSerializer, Configuration configuration,
			Configuration nextConfiguration) {
		return addFilteredLinks(model, Map.of(//
				Rel.Iana.SELF,
				() -> methodOn(CharacterBannerController.class).getRun(configurationSerializer.apply(configuration)), //
				Rel.Banners.CONFIGURATION,
				() -> methodOn(CharacterBannerController.class)
						.getConfiguration(configurationSerializer.apply(configuration)), //
				Rel.Banners.NEXT_CONFIGURATION,
				() -> methodOn(CharacterBannerController.class)
						.getConfiguration(configurationSerializer.apply(nextConfiguration)), //
				Rel.Banners.NEXT_RUN,
				() -> methodOn(CharacterBannerController.class)
						.getRun(configurationSerializer.apply(nextConfiguration)), //
				Rel.Banners.NEXT_MULTI, () -> methodOn(CharacterBannerController.class)
						.getMulti(configurationSerializer.apply(nextConfiguration))//
		));
	}

	public CollectionModel<Wish> decorateCharactersBannerMulti(//
			CollectionModel<Wish> model, //
			Function<Configuration, String> configurationSerializer, Configuration configuration,
			Configuration nextConfiguration) {
		return addFilteredLinks(model, Map.of(//
				Rel.Iana.SELF,
				() -> methodOn(CharacterBannerController.class).getMulti(configurationSerializer.apply(configuration)), //
				Rel.Banners.CONFIGURATION,
				() -> methodOn(CharacterBannerController.class)
						.getConfiguration(configurationSerializer.apply(configuration)), //
				Rel.Banners.NEXT_CONFIGURATION,
				() -> methodOn(CharacterBannerController.class)
						.getConfiguration(configurationSerializer.apply(nextConfiguration)), //
				Rel.Banners.NEXT_RUN,
				() -> methodOn(CharacterBannerController.class)
						.getRun(configurationSerializer.apply(nextConfiguration)), //
				Rel.Banners.NEXT_MULTI, () -> methodOn(CharacterBannerController.class)
						.getMulti(configurationSerializer.apply(nextConfiguration))//
		));
	}

	public EntityModel<Wish.Stats> decorateCharactersBannerStats(//
			EntityModel<Wish.Stats> model, //
			Function<Configuration, String> configurationSerializer, Configuration configuration) {
		return addFilteredLinks(model, Map.of(//
				Rel.Iana.SELF,
				() -> methodOn(CharacterBannerController.class).getStats(configurationSerializer.apply(configuration)), //
				Rel.Banners.CONFIGURATION, () -> methodOn(CharacterBannerController.class)
						.getConfiguration(configurationSerializer.apply(configuration))//
		));
	}

	private <T extends RepresentationModel<?>> T addFilteredLinks(T model, Map<LinkRelation, Supplier<?>> relations) {
		return addFilteredLinks(model, relations, List.of());
	}

	private <T extends RepresentationModel<?>> T addFilteredLinks(T model, Map<LinkRelation, Supplier<?>> relations,
			List<Supplier<?>> selfAffordances) {
		relations.forEach((relation, resourceMethodSupplier) -> {
			if (relationFilter.test(relation)) {
				Link link = linkTo(resourceMethodSupplier.get()).withRel(relation);
				if (relation == Rel.Iana.SELF) {
					List<Affordance> affordances = selfAffordances.stream()//
							.map(Supplier::get)//
							.map(WebMvcLinkBuilder::afford)//
							.collect(Collectors.toList());
					link = link.andAffordances(affordances);
				}
				model.add(link);
			}
		});
		return model;
	}

}
