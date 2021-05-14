package fr.sazaju.genshin.material;

public class MaterialCount<T extends MaterialType> {
	public final Material<T> material;
	public final int count;

	public MaterialCount(Material<T> material, int count) {
		this.material = material;
		this.count = count;
	}

	@Override
	public String toString() {
		return material + " x" + count;
	}
}
