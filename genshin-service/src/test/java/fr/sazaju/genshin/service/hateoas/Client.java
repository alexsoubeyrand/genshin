package fr.sazaju.genshin.service.hateoas;

public class Client {

	private final String rootRelativePath;

	public Client(String rootRelativePath) {
		this.rootRelativePath = rootRelativePath;
	}

	public Caller resolvePath(String relativeUrl) {
		return new Caller(relativeUrl);
	}

	public Response callRoot() {
		return resolvePath(rootRelativePath).call();
	}

}
