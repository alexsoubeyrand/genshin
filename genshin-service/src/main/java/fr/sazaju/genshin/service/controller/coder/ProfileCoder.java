package fr.sazaju.genshin.service.controller.coder;

import static fr.sazaju.genshin.service.controller.coder.VersionCoder.*;

import java.io.IOException;
import java.util.stream.Stream;

import fr.sazaju.genshin.service.controller.coder.BytesCoder.Option;
import fr.sazaju.genshin.simulator.wish.Profile;

public enum ProfileCoder implements Coder<Profile, String> {
	ALL_VALUES_SEQUENTIAL(1, new Definition<>(//
			Integer.BYTES + Integer.BYTES + Byte.BYTES, //
			(profile, output) -> {
				output.writeInt(profile.wishesLessThan4Stars);
				output.writeInt(profile.wishesLessThan5Stars);
				output.writeBoolean(profile.isExclusiveGuaranteedOnNext5Stars);
			}, //
			(input) -> {
				return new Profile(//
						input.readInt(), //
						input.readInt(), //
						input.readBoolean());
			}//
	)), //
	ALL_VALUES_SEQUENTIAL_COMPRESSED(2, ALL_VALUES_SEQUENTIAL.definition, Option.GZIP);

	private final Definition<Profile> definition;
	private final VersionCoder<Profile> versionCoder;

	ProfileCoder(int version, Definition<Profile> definition, BytesCoder.Option... options) {
		this.definition = definition;
		BytesCoder<Profile> dataCoder = new BytesCoder<>(definition, options);
		this.versionCoder = new VersionCoder<>(version, dataCoder);
	}

	@Override
	public String encode(Profile profile) throws IOException {
		return versionCoder.encode(profile);
	}

	@Override
	public Profile decode(String serial) throws IOException {
		return versionCoder.decode(serial);
	}

	public static ProfileCoder fromSerial(String serial) {
		return Stream.of(values())//
				.filter(fromSerialVersion(serial, profileCoder -> profileCoder.versionCoder))//
				.findFirst().orElseThrow();
	}

	public static String generateShortestSerial(Profile profile) {
		return Coder.generateShortestSerial(profile, Stream.of(values()));
	}

}
