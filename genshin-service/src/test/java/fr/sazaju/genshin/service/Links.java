package fr.sazaju.genshin.service;

public class Links {

	public static class About {
		public static final String ABOUT = "about";
		public static final String AUTHOR = "author";
		public static final String SOURCE = customRel("source");
	}

	public static class Packs {
		public static final String PACKS = customRel("packs");
		public static final String FIRST_ORDER = customRel("first-order");
		public static final String NEXT_ORDERS = customRel("next-orders");
	}

	public static class Banners {
		public static final String CHARACTER_BANNER = customRel("character-banner");
		public static final String CONFIGURATION = customRel("configuration");
		public static final String NEXT_RUN = customRel("next-run");
		public static final String NEXT_MULTI = customRel("next-multi");
	}

	private static String customRel(String rel) {
		return "http://localhost:8080/rels/" + rel;
	}
}
