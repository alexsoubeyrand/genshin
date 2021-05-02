package fr.sazaju.genshin.service.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Map;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.sazaju.genshin.service.Rel;

@Controller
//@EnableHypermediaSupport(type = HypermediaType.HAL)
@RequestMapping(value = "/", produces = "application/hal+json")
public class MainController {
	@GetMapping
	@ResponseBody
	public EntityModel<?> getServiceRoot() {
		EntityModel<Object> model = EntityModel.of(Map.of());
		model.add(linkTo(methodOn(MainController.class).getServiceRoot()).withRel(Rel.Iana.SELF));
		model.add(linkTo(methodOn(MainController.class).getAbout()).withRel(Rel.Iana.ABOUT));
		model.add(linkTo(methodOn(PackController.class).getPacks()).withRel(Rel.Packs.PACKS));
		model.add(linkTo(methodOn(CharacterBannerController.class).getBanner()).withRel(Rel.Banners.CHARACTER_BANNER));
		return model;
	}

	@GetMapping("/about")
	@ResponseBody
	public EntityModel<?> getAbout() {
		EntityModel<Object> model = EntityModel.of(Map.of());
		model.add(linkTo(methodOn(MainController.class).getAbout()).withRel(Rel.Iana.SELF));
		model.add(Link.of("mailto:sazaju@gmail.com", Rel.Iana.AUTHOR));
		model.add(Link.of("https://github.com/Sazaju/genshin", Rel.Global.SOURCE));
		return model;
	}

}
