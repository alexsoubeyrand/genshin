package fr.sazaju.genshin.service.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Map;
import java.util.function.Predicate;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;

import fr.sazaju.genshin.model.Pack;
import fr.sazaju.genshin.service.Rel;

public class Linker {
	private final Predicate<LinkRelation> relationFilter;

	private Linker(Predicate<LinkRelation> relationFilter) {
		this.relationFilter = relationFilter;
	}

	public static Linker allLinks() {
		return new Linker(relation -> true);
	}

	public static Linker selfLink() {
		return new Linker(Predicate.isEqual(Rel.SELF));
	}

	public EntityModel<Pack> decoratePack(EntityModel<Pack> model) {
		Pack pack = model.getContent();
		String euros = "" + pack.euros;
		return addFilteredLinks(model, Map.of(//
				Rel.SELF, methodOn(PackController.class).getPackByEuros(euros), //
				Rel.FIRST_ORDER, methodOn(PackController.class).getFirstOrderPackByEuros(euros), //
				Rel.PACKS, methodOn(PackController.class).getPacks()//
		));
	}

	public EntityModel<Pack> decorateFirstOrderPack(EntityModel<Pack> model) {
		Pack pack = model.getContent();
		String euros = "" + pack.euros;
		return addFilteredLinks(model, Map.of(//
				Rel.SELF, methodOn(PackController.class).getFirstOrderPackByEuros(euros), //
				Rel.NEXT_ORDERS, methodOn(PackController.class).getPackByEuros(euros), //
				Rel.PACKS, methodOn(PackController.class).getFirstOrderPacks()//
		));
	}

	public CollectionModel<EntityModel<Pack>> decoratePackCollection(CollectionModel<EntityModel<Pack>> model) {
		return addFilteredLinks(model, Map.of(//
				Rel.SELF, methodOn(PackController.class).getPacks(), //
				Rel.FIRST_ORDER, methodOn(PackController.class).getFirstOrderPacks() //
		));
	}

	public CollectionModel<EntityModel<Pack>> decorateFirstOrderPackCollection(
			CollectionModel<EntityModel<Pack>> model) {
		return addFilteredLinks(model, Map.of(//
				Rel.SELF, methodOn(PackController.class).getFirstOrderPacks(), //
				Rel.NEXT_ORDERS, methodOn(PackController.class).getPacks() //
		));
	}

	private <T extends RepresentationModel<?>> T addFilteredLinks(T model, Map<LinkRelation, ?> relations) {
		relations.forEach((relation, resource) -> {
			if (relationFilter.test(relation)) {
				model.add(linkTo(resource).withRel(relation));
			}
		});
		return model;
	}

}
