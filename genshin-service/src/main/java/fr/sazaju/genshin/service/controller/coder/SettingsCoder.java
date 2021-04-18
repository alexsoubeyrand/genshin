package fr.sazaju.genshin.service.controller.coder;

import java.io.IOException;
import java.util.stream.Stream;

import fr.sazaju.genshin.service.controller.coder.BytesCoder.Option;
import fr.sazaju.genshin.simulator.wish.Settings;

public enum SettingsCoder implements Coder<Settings, String> {
	BASIC(1, new BytesCoder<>(//
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
	COMPRESSED(2, new BytesCoder<>(//
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
			}, //
			Option.GZIP));

	private final int version;
	private final Coder<Settings, String> coder;

	SettingsCoder(int version, Coder<Settings, byte[]> profileCoder) {
		this.version = version;
		this.coder = VersionCodingUtils.createVersionCoder(version, profileCoder);
	}

	@Override
	public String encode(Settings profile) throws IOException {
		return coder.encode(profile);
	}

	@Override
	public Settings decode(String serial) throws IOException {
		return coder.decode(serial);
	}

	public static Coder<Settings, String> fromSerial(String serial) {
		return VersionCodingUtils.fromSerial(serial, Stream.of(values()), coder -> coder.version);
	}

	public static String generateShortestSerial(Settings profile) {
		return VersionCodingUtils.generateShortestSerial(profile, Stream.of(values()));
	}

}
