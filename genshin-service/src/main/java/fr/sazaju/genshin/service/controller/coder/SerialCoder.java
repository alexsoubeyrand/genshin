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

	private final int id;
	private final Coder<D, byte[]> dataCoder;

	public SerialCoder(int id, Coder<D, byte[]> dataCoder) {
		this.id = id;
		this.dataCoder = dataCoder;
	}

	@Override
	public String encode(D data) throws IOException {
		byte[] dataBytes = dataCoder.encode(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(baos)) {
			dos.writeInt(id);
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
			int readId = dis.readInt();
			if (readId != id) {
				throw new InvalidCoderIdException(id, readId);
			}
			byte[] dataBytes = dis.readAllBytes();
			return dataCoder.decode(dataBytes);
		}
	}

	public static <T> Predicate<? super T> fromSerial(String serial, Function<T, SerialCoder<?>> coderExtractor) {
		byte[] bytes = Base64.getUrlDecoder().decode(serial);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		int id;
		try (DataInputStream dis = new DataInputStream(bais)) {
			id = dis.readInt();
		} catch (IOException cause) {
			throw new CannotFindCoderIdException(cause);
		}
		return target -> coderExtractor.apply(target).id == id;
	}
}
