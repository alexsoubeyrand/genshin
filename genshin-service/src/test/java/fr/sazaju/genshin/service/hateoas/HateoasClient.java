package fr.sazaju.genshin.service.hateoas;

public class HateoasClient {

	private final String rootRelativePath;

	public HateoasClient(String rootRelativePath) {
		this.rootRelativePath = rootRelativePath;
	}
	
	public HateoasClient() {
		this("");
	}

	public Caller resolvePath(String relativeUrl) {
		return new Caller(relativeUrl);
	}

	public Response callRoot() {
		return resolvePath(rootRelativePath).call();
	}

}
