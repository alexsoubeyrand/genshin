package fr.sazaju.genshin.service.controller.coder;

import static fr.sazaju.genshin.service.controller.coder.VersionCoder.*;

import java.io.IOException;
import java.util.stream.Stream;

import fr.sazaju.genshin.service.controller.coder.BytesCoder.Option;
import fr.sazaju.genshin.simulator.wish.Settings;

public enum SettingsCoder implements Coder<Settings, String> {
	SEQUENTIAL(1, SettingsDefinition.SEQUENTIAL_VALUES), //
	SEQUENTIAL_COMPRESSED(2, SEQUENTIAL.definition, Option.GZIP);

	private final Definition<Settings> definition;
	private final VersionCoder<Settings> versionCoder;

	SettingsCoder(int version, Definition<Settings> definition, BytesCoder.Option... options) {
		this.definition = definition;
		BytesCoder<Settings> dataCoder = new BytesCoder<>(definition, options);
		this.versionCoder = new VersionCoder<>(version, dataCoder);
	}

	@Override
	public String encode(Settings profile) throws IOException {
		return versionCoder.encode(profile);
	}

	@Override
	public Settings decode(String serial) throws IOException {
		return versionCoder.decode(serial);
	}

	public static SettingsCoder fromSerial(String serial) {
		return Stream.of(values())//
				.filter(fromSerialVersion(serial, settingsCoder -> settingsCoder.versionCoder))//
				.findFirst().orElseThrow();
	}

	public static String generateShortestSerial(Settings profile) {
		return Coder.generateShortestSerial(profile, Stream.of(values()));
	}

}
