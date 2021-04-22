package fr.sazaju.genshin.service.controller.coder;

import static fr.sazaju.genshin.service.controller.coder.ProfileDefinition.*;

import java.io.IOException;
import java.util.stream.Stream;

import fr.sazaju.genshin.service.controller.coder.SequentialCoder.Option;
import fr.sazaju.genshin.simulator.wish.Profile;

public enum ProfileCoder implements Coder<Profile, String> {
	SEQUENTIAL(1, new SequentialCoder<>(V1)), //
	SEQUENTIAL_COMPRESSED(2, new SequentialCoder<>(V1, Option.GZIP));

	private final SerialCoder<Profile> serialCoder;

	private ProfileCoder(int version, Coder<Profile, byte[]> bytesCoder) {
		this.serialCoder = new SerialCoder<>(version, bytesCoder);
	}

	@Override
	public String encode(Profile profile) throws IOException {
		return serialCoder.encode(profile);
	}

	@Override
	public Profile decode(String serial) throws IOException {
		return serialCoder.decode(serial);
	}

	public static ProfileCoder fromSerial(String serial) {
		return Stream.of(values())//
				.filter(SerialCoder.fromSerial(serial, profileCoder -> profileCoder.serialCoder))//
				.findFirst().orElseThrow();
	}

	public static String generateShortestSerial(Profile profile) {
		return Coder.generateShortestSerial(profile, Stream.of(values()));
	}

}
