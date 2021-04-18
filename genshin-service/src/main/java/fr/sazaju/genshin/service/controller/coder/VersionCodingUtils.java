package fr.sazaju.genshin.service.controller.coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

public class VersionCodingUtils {
	public static <T> Coder<T, String> createVersionCoder(int version, Coder<T, byte[]> dataCoder) {
		return new Coder<T, String>() {

			@Override
			public String encode(T data) throws IOException {
				byte[] dataBytes = dataCoder.encode(data);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try (DataOutputStream dos = new DataOutputStream(baos)) {
					dos.writeInt(version);
					dos.write(dataBytes);
				}
				byte[] bytes = baos.toByteArray();
				return Base64.getUrlEncoder().encodeToString(bytes);
			}

			@Override
			public T decode(String serial) throws IOException {
				byte[] bytes = Base64.getUrlDecoder().decode(serial);
				ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				try (DataInputStream dis = new DataInputStream(bais)) {
					int readVersion = dis.readInt();
					if (readVersion != version) {
						throw new InvalidCoderVersionException(version, readVersion);
					}
					byte[] dataBytes = dis.readAllBytes();
					return dataCoder.decode(dataBytes);
				}
			}

		};
	}

	public static <C extends Coder<?, ?>> C fromSerial(String serial, Stream<C> codersStream, Function<C, Integer> versionReader) {
		byte[] bytes = Base64.getUrlDecoder().decode(serial);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		int version;
		try (DataInputStream dis = new DataInputStream(bais)) {
			version = dis.readInt();
		} catch (IOException cause) {
			throw new CannotFindCoderVersionException(cause);
		}
		return codersStream//
				.filter(coder -> versionReader.apply(coder) == version)//
				.findFirst().orElseThrow();
	}

	public static <T> String generateShortestSerial(T data, Stream<Coder<T, String>> codersStream) {
		return codersStream//
				.map(coder -> {
					try {
						return coder.encode(data);
					} catch (IOException cause) {
						throw new RuntimeException(cause);
					}
				})//
				.sorted(Comparator.comparing(String::length))//
				.findFirst().orElseThrow(() -> {
					return new RuntimeException("No encoder found for " + data);
				});
	}
}
