package fr.sazaju.genshin.service.controller.coder;

import static fr.sazaju.genshin.service.controller.coder.StateDefinition.*;

import java.io.IOException;
import java.util.stream.Stream;

import fr.sazaju.genshin.service.controller.coder.SequentialCoder.Option;
import fr.sazaju.genshin.simulator.wish.State;

public enum StateCoder implements Coder<State, String> {
	SEQUENTIAL(1, new SequentialCoder<>(V1)), //
	SEQUENTIAL_COMPRESSED(2, new SequentialCoder<>(V1, Option.GZIP));

	private final SerialCoder<State> serialCoder;

	private StateCoder(int version, Coder<State, byte[]> bytesCoder) {
		this.serialCoder = new SerialCoder<>(version, bytesCoder);
	}

	@Override
	public String encode(State state) throws IOException {
		return serialCoder.encode(state);
	}

	@Override
	public State decode(String serial) throws IOException {
		return serialCoder.decode(serial);
	}

	public static StateCoder fromSerial(String serial) {
		return Stream.of(values())//
				.filter(SerialCoder.fromSerial(serial, profileCoder -> profileCoder.serialCoder))//
				.findFirst().orElseThrow();
	}

	public static String generateShortestSerial(State state) {
		return Coder.generateShortestSerial(state, Stream.of(values()));
	}

}
