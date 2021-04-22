package fr.sazaju.genshin.service.controller.coder;

import static fr.sazaju.genshin.service.controller.coder.SettingsDefinition.*;

import java.io.IOException;
import java.util.stream.Stream;

import fr.sazaju.genshin.service.controller.coder.SequentialCoder.Option;
import fr.sazaju.genshin.simulator.wish.Settings;

public enum SettingsCoder implements Coder<Settings, String> {
	SEQUENTIAL(1, new SequentialCoder<>(V1)), //
	SEQUENTIAL_COMPRESSED(2, new SequentialCoder<>(V1, Option.GZIP));

	private final SerialCoder<Settings> serialCoder;

	private SettingsCoder(int version, Coder<Settings, byte[]> bytesCoder) {
		this.serialCoder = new SerialCoder<>(version, bytesCoder);
	}

	@Override
	public String encode(Settings settings) throws IOException {
		return serialCoder.encode(settings);
	}

	@Override
	public Settings decode(String serial) throws IOException {
		return serialCoder.decode(serial);
	}

	public static SettingsCoder fromSerial(String serial) {
		return Stream.of(values())//
				.filter(SerialCoder.fromSerial(serial, settingsCoder -> settingsCoder.serialCoder))//
				.findFirst().orElseThrow();
	}

	public static String generateShortestSerial(Settings settings) {
		return Coder.generateShortestSerial(settings, Stream.of(values()));
	}

}
