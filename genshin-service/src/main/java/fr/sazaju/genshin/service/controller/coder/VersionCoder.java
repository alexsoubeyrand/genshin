package fr.sazaju.genshin.service.controller.coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.function.Function;
import java.util.function.Predicate;

public class VersionCoder<T> implements Coder<T, String> {

	private final int version;
	private final Coder<T, byte[]> dataCoder;

	public VersionCoder(int version, Coder<T, byte[]> dataCoder) {
		this.version = version;
		this.dataCoder = dataCoder;
	}

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

	public static <T> Predicate<? super T> fromSerialVersion(String serial, Function<T, VersionCoder<?>> coderExtractor) {
		byte[] bytes = Base64.getUrlDecoder().decode(serial);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		int version;
		try (DataInputStream dis = new DataInputStream(bais)) {
			version = dis.readInt();
		} catch (IOException cause) {
			throw new CannotFindCoderVersionException(cause);
		}
		return target -> coderExtractor.apply(target).version == version;
	}
}
