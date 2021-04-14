package fr.sazaju.genshin.service.controller;

import static fr.sazaju.genshin.service.controller.Linker.*;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/banners", produces = "application/hal+json")
public class BannerController {

	@GetMapping
	@ResponseBody
	public CollectionModel<?> getBanners() {
		return allLinks().decorateBannerCollection(CollectionModel.of(List.of()));
	}

	@GetMapping("/characters")
	@ResponseBody
	public CollectionModel<?> getCharactersBanner() {
		return allLinks().decorateCharactersBanner(CollectionModel.of(List.of()));
	}

	@PostMapping("/characters/settings")
	@ResponseBody
	public CollectionModel<?> postCharactersBannerSettings() {
		return allLinks().decorateCharactersBannerSettings(CollectionModel.of(List.of()));
	}

	@GetMapping("/characters/settings")
	@ResponseBody
	public CollectionModel<?> getCharactersBannerSettings() {
		return allLinks().decorateCharactersBannerSettings(CollectionModel.of(List.of()));
	}
}
