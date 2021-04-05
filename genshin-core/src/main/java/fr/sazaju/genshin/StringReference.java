package fr.sazaju.genshin;

public enum StringReference {
	STAR("☆"),//
	
	EXCLUSIVE("exclusive"),//
	PERMANENT("permanent"),//
	
	WEAPON("weapon"),//
	CHARACTER("charac");

	private final String string;

	StringReference(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return string;
	}

}
