package fr.sazaju.genshin.service.controller.coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BytesCoder<T> implements Coder<T, byte[]> {
	enum Option {
		GZIP(GZIPOutputStream::new, GZIPInputStream::new);

		private final OutputStreamDecorator outputStreamDecorator;
		private final InputStreamDecorator inputStreamDecorator;

		private Option(OutputStreamDecorator outputStreamDecorator, InputStreamDecorator inputStreamDecorator) {
			this.outputStreamDecorator = outputStreamDecorator;
			this.inputStreamDecorator = inputStreamDecorator;
		}

		OutputStream decorateOutputStream(OutputStream out) throws IOException {
			return outputStreamDecorator.apply(out);
		}

		InputStream decorateInputStream(InputStream in) throws IOException {
			return inputStreamDecorator.apply(in);
		}
	}

	interface OutputStreamDecorator {
		OutputStream apply(OutputStream out) throws IOException;
	}

	interface InputStreamDecorator {
		InputStream apply(InputStream in) throws IOException;
	}

	private final Definition<T> definition;
	private final List<Option> options;

	public interface Writer<T> {
		void write(T data, DataOutputStream output) throws IOException;
	}

	public interface Reader<T> {
		T read(DataInputStream input) throws IOException;
	}

	public static class Definition<T> {
		public final int bytes_count;
		public final Writer<T> writer;
		public final Reader<T> reader;

		public Definition(int bytes_count, Writer<T> writer, Reader<T> reader) {
			this.bytes_count = bytes_count;
			this.writer = writer;
			this.reader = reader;
		}
	}

	public BytesCoder(Definition<T> definition, Option... options) {
		this.definition = definition;
		this.options = List.of(options);
	}

	public BytesCoder(int bytes_count, Writer<T> writer, Reader<T> reader, Option... options) {
		this(new Definition<>(bytes_count, writer, reader), options);
	}

	@Override
	public byte[] encode(T data) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(definition.bytes_count);
		OutputStream out = baos;
		for (Option option : options) {
			out = option.decorateOutputStream(out);
		}
		try (DataOutputStream dos = new DataOutputStream(out)) {
			definition.writer.write(data, dos);
		}
		return baos.toByteArray();
	}

	@Override
	public T decode(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		InputStream in = bais;
		for (Option option : options) {
			in = option.decorateInputStream(in);
		}
		try (DataInputStream dis = new DataInputStream(in)) {
			return definition.reader.read(dis);
		}
	}
}
