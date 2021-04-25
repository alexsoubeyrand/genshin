package fr.sazaju.genshin.service.controller.coder;

import static fr.sazaju.genshin.service.controller.coder.SimulatorDefinition.*;

import java.io.IOException;
import java.util.stream.Stream;

import fr.sazaju.genshin.service.controller.coder.SequentialCoder.Option;
import fr.sazaju.genshin.service.controller.coder.SimulatorDefinition.Simulator;

public enum SimulatorCoder implements Coder<Simulator, String> {
	SEQUENTIAL(1, new SequentialCoder<>(V1)), //
	SEQUENTIAL_COMPRESSED(2, new SequentialCoder<>(V1, Option.GZIP));

	private final SerialCoder<Simulator> serialCoder;

	private SimulatorCoder(int version, Coder<Simulator, byte[]> bytesCoder) {
		this.serialCoder = new SerialCoder<>(version, bytesCoder);
	}

	@Override
	public String encode(Simulator simulator) throws IOException {
		return serialCoder.encode(simulator);
	}

	@Override
	public Simulator decode(String serial) throws IOException {
		return serialCoder.decode(serial);
	}

	public static SimulatorCoder fromSerial(String serial) {
		return Stream.of(values())//
				.filter(SerialCoder.fromSerial(serial, SimulatorCoder -> SimulatorCoder.serialCoder))//
				.findFirst().orElseThrow();
	}

	public static String generateShortestSerial(Simulator simulator) {
		return Coder.generateShortestSerial(simulator, Stream.of(values()));
	}

}
