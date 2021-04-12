package fr.sazaju.genshin.service.hateoas;

public class Hateoas {

	public static Client createClient(String rootRelativePath) {
		return new Client(rootRelativePath);
	}

}
