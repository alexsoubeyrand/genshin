package fr.sazaju.genshin.service.controller.coder;

import static fr.sazaju.genshin.service.controller.coder.ConfigurationDefinition.*;

import java.io.IOException;
import java.util.stream.Stream;

import fr.sazaju.genshin.service.controller.coder.SequentialCoder.Option;
import fr.sazaju.genshin.service.controller.coder.ConfigurationDefinition.Configuration;

public enum ConfigurationCoder implements Coder<Configuration, String> {
	SEQUENTIAL(1, new SequentialCoder<>(V1)), //
	SEQUENTIAL_COMPRESSED(2, new SequentialCoder<>(V1, Option.GZIP));

	private final SerialCoder<Configuration> serialCoder;

	private ConfigurationCoder(int version, Coder<Configuration, byte[]> bytesCoder) {
		this.serialCoder = new SerialCoder<>(version, bytesCoder);
	}

	@Override
	public String encode(Configuration configuration) throws IOException {
		return serialCoder.encode(configuration);
	}

	@Override
	public Configuration decode(String serial) throws IOException {
		return serialCoder.decode(serial);
	}

	public static ConfigurationCoder fromSerial(String serial) {
		return Stream.of(values())//
				.filter(SerialCoder.fromSerial(serial, SimulatorCoder -> SimulatorCoder.serialCoder))//
				.findFirst().orElseThrow();
	}

	public static String generateShortestSerial(Configuration configuration) {
		return Coder.generateShortestSerial(configuration, Stream.of(values()));
	}

}
