package fr.sazaju.genshin.service.controller.coder;

import java.io.IOException;

public interface Coder<T, U> {

	U encode(T data) throws IOException;

	T decode(U serial) throws IOException;

}