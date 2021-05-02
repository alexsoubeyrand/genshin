package fr.sazaju.genshin.service.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.sazaju.genshin.service.model.Product;
import fr.sazaju.genshin.service.model.ProductPatch;

@Controller
@RequestMapping(value = "/", produces = "application/prs.hal-forms+json")
public class ProductController {

	@GetMapping("/product")
	@ResponseBody
	public EntityModel<Product> getProduct() {
		Product product = getDefaultProduct();

		EntityModel<Product> model = EntityModel.of(product);
		ProductController controller = methodOn(ProductController.class);
		model.add(linkTo(controller.getProduct()).withRel(IanaLinkRelations.SELF)//
				.andAffordance(afford(controller.patchProduct(null))));

		return model;
	}

	@PatchMapping("/product")
	@ResponseBody
	public EntityModel<Product> patchProduct(@RequestBody ProductPatch patch) {
		Product product = getDefaultProduct();

		if (patch.newName != null) {
			product.name = patch.newName;
		}
		if (patch.newPrice != null) {
			product.price = patch.newPrice;
		}

		EntityModel<Product> model = EntityModel.of(product);
		ProductController controller = methodOn(ProductController.class);
		model.add(linkTo(controller.getProduct()).withRel(IanaLinkRelations.SELF)//
				.andAffordance(afford(controller.patchProduct(null))));

		return model;
	}

	private Product getDefaultProduct() {
		Product product = new Product();
		product.name = "my product";
		product.price = 10.0;
		return product;
	}

}
