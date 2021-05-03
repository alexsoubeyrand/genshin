package fr.sazaju.genshin.service.model;

public interface Modifier<T> {
	T apply(T source);
}
