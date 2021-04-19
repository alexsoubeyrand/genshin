package fr.sazaju.genshin.service.controller.coder;

import static fr.sazaju.genshin.service.controller.coder.VersionCoder.*;

import java.io.IOException;
import java.util.stream.Stream;

import fr.sazaju.genshin.service.controller.coder.BytesCoder.Definition;
import fr.sazaju.genshin.service.controller.coder.BytesCoder.Option;
import fr.sazaju.genshin.simulator.wish.Settings;

public enum SettingsCoder implements Coder<Settings, String> {
	ALL_VALUES_SEQUENTIAL(1, new BytesCoder.Definition<>(//
			4 * Double.BYTES + 2 * Integer.BYTES, //
			(settings, output) -> {
				output.writeDouble(settings.probability4Stars);
				output.writeDouble(settings.probability4StarsWeaponCharacter);
				output.writeDouble(settings.probability5Stars);
				output.writeDouble(settings.probability5StarsPermanentExclusive);
				output.writeInt(settings.guaranty4Stars);
				output.writeInt(settings.guaranty5Stars);
			}, //
			(input) -> {
				return Settings.build()//
						.withProbability4Stars(input.readDouble())//
						.withProbability4StarsWeaponCharacter(input.readDouble())//
						.withProbability5Stars(input.readDouble())//
						.withProbability5StarsPermanentExclusive(input.readDouble())//
						.withGuaranty4Stars(input.readInt())//
						.withGuaranty5Stars(input.readInt())//
						.create();
			}//
	)), //
	ALL_VALUES_SEQUENTIAL_COMPRESSED(2, ALL_VALUES_SEQUENTIAL.definition, Option.GZIP);

	private final Definition<Settings> definition;
	private final VersionCoder<Settings> versionCoder;

	SettingsCoder(int version, BytesCoder.Definition<Settings> definition, BytesCoder.Option... options) {
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
