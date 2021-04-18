package fr.sazaju.genshin.service.controller.coder;

import java.io.IOException;
import java.util.stream.Stream;

import fr.sazaju.genshin.service.controller.coder.BytesCoder.Option;
import fr.sazaju.genshin.simulator.wish.Profile;

public enum ProfileCoder implements Coder<Profile, String> {
	BASIC(1, new BytesCoder<>(//
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
	COMPRESSED(2, new BytesCoder<>(//
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
			}, //
			Option.GZIP));

	private final int versionNum;
	private final Coder<Profile, String> coder;

	ProfileCoder(int version, Coder<Profile, byte[]> profileCoder) {
		this.versionNum = version;
		this.coder = VersionCodingUtils.createVersionCoder(version, profileCoder);
	}

	@Override
	public String encode(Profile profile) throws IOException {
		return coder.encode(profile);
	}

	@Override
	public Profile decode(String serial) throws IOException {
		return coder.decode(serial);
	}

	public static Coder<Profile, String> fromSerial(String serial) {
		return VersionCodingUtils.fromSerial(serial, Stream.of(values()), version -> version.versionNum);
	}

	public static String generateShortestSerial(Profile profile) {
		return VersionCodingUtils.generateShortestSerial(profile, Stream.of(values()));
	}

}
