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

import fr.sazaju.genshin.service.controller.coder.Definition.DataReader;
import fr.sazaju.genshin.service.controller.coder.Definition.DataWriter;

public class BytesCoder<D> implements Coder<D, byte[]> {
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

	private final Definition<D> definition;
	private final List<Option> options;

	public BytesCoder(Definition<D> definition, Option... options) {
		this.definition = definition;
		this.options = List.of(options);
	}

	public BytesCoder(int bytes_count, DataWriter<DataOutputStream, D> writer, DataReader<DataInputStream, D> reader,
			Option... options) {
		this(new Definition<>(bytes_count, writer, reader), options);
	}

	@Override
	public byte[] encode(D data) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(definition.bytes_count);
		OutputStream out = baos;
		for (Option option : options) {
			out = option.decorateOutputStream(out);
		}
		try (DataOutputStream dos = new DataOutputStream(out)) {
			definition.dataWriter.writeData(data, dos);
		}
		return baos.toByteArray();
	}

	@Override
	public D decode(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		InputStream in = bais;
		for (Option option : options) {
			in = option.decorateInputStream(in);
		}
		try (DataInputStream dis = new DataInputStream(in)) {
			return definition.dataReader.readData(dis);
		}
	}
}
