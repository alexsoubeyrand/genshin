package fr.sazaju.genshin.service.controller.coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.function.Function;
import java.util.function.Predicate;

public class SerialCoder<D> implements Coder<D, String> {

	private final int version;
	private final Coder<D, byte[]> dataCoder;

	public SerialCoder(int version, Coder<D, byte[]> dataCoder) {
		this.version = version;
		this.dataCoder = dataCoder;
	}

	@Override
	public String encode(D data) throws IOException {
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
	public D decode(String serial) throws IOException {
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

	public static <T> Predicate<? super T> fromSerial(String serial, Function<T, SerialCoder<?>> coderExtractor) {
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
