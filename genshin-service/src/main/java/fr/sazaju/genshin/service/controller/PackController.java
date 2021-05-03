package fr.sazaju.genshin.service.controller;

import static fr.sazaju.genshin.service.controller.Linker.*;

import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.sazaju.genshin.model.Pack;

@Controller
@RequestMapping(value = "/packs", produces = "application/prs.hal-forms+json")
public class PackController {

	@GetMapping
	@ResponseBody
	public CollectionModel<EntityModel<Pack>> getPacks() {
		CollectionModel<EntityModel<Pack>> packs = CollectionModel.of(Pack.getAllPacks()//
				.stream()//
				.map(this::toEntityModel)//
				.map(selfLink()::decoratePack)//
				.collect(Collectors.toList()));
		return allLinks().decoratePackCollection(packs);
	}

	@GetMapping("/byEuros/{euros}")
	@ResponseBody
	public EntityModel<?> getPackByEuros(@PathVariable String euros) {
		return Pack.getAllPacks()//
				.stream()//
				.filter(pack -> pack.euros == Float.parseFloat(euros))//
				.map(this::toEntityModel)//
				.map(allLinks()::decoratePack)//
				.findFirst().orElseThrow(() -> new UnknownResourceException("pack at €" + euros));
	}

	@GetMapping("/firstOrder")
	@ResponseBody
	public CollectionModel<EntityModel<Pack>> getFirstOrderPacks() {
		CollectionModel<EntityModel<Pack>> packs = CollectionModel.of(Pack.getAllPacks()//
				.stream()//
				.map(Pack::createFirstOrderVariant)//
				.map(this::toEntityModel)//
				.map(selfLink()::decorateFirstOrderPack)//
				.collect(Collectors.toList()));
		return allLinks().decorateFirstOrderPackCollection(packs);
	}

	@GetMapping("/firstOrder/byEuros/{euros}")
	@ResponseBody
	public EntityModel<?> getFirstOrderPackByEuros(@PathVariable String euros) {
		return Pack.getAllPacks()//
				.stream()//
				.filter(pack -> pack.euros == Float.parseFloat(euros))//
				.map(Pack::createFirstOrderVariant)//
				.map(this::toEntityModel)//
				.map(allLinks()::decorateFirstOrderPack)//
				.findFirst().orElseThrow(() -> new UnknownResourceException("pack at €" + euros));
	}

	private <T> EntityModel<T> toEntityModel(T resource) {
		return EntityModel.of(resource);
	}

}
