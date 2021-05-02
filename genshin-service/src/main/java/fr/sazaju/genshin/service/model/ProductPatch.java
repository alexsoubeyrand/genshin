package fr.sazaju.genshin.service.model;

import org.springframework.hateoas.RepresentationModel;

public class ProductPatch extends RepresentationModel<ProductPatch> {
	public String newName;
	public Double newPrice;
}
