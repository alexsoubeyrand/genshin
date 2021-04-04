package fr.sazaju.genshin.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CsvParser {
	private final static Function<String, String[]> LINE_SPLITER = line -> line.split(",");

	public interface CsvRowValues {
		String get(String key);
	}

	public interface CsvRowParser<T> {
		T parse(CsvRowValues values);
	}

	public <T> List<T> parse(InputStream inputStream, CsvRowParser<T> rowParser) throws IOException {
		try (BufferedReader reader = createUtf8Reader(inputStream)) {
			List<String> headers = List.of(LINE_SPLITER.apply(reader.readLine()));
			Function<String, T> lineParser = createLineParser(headers, rowParser);
			return reader.lines()// Read remaining lines
					.filter(line -> !line.isEmpty())// Ignore empty lines
					.map(lineParser::apply)//
					.collect(Collectors.toList());
		}
	}

	private <T> Function<String, T> createLineParser(List<String> headers, CsvRowParser<T> rowParser) {
		Map<String, Integer> indexes = new HashMap<>();
		return line -> {
			List<String> csvValues = List.of(LINE_SPLITER.apply(line));
			CsvRowValues values = key -> csvValues.get(indexes.computeIfAbsent(key, headers::indexOf));
			return rowParser.parse(values);
		};
	}

	private BufferedReader createUtf8Reader(InputStream inputStream) {
		InputStreamReader streamReader = new InputStreamReader(inputStream, Charset.forName("UTF8"));
		return new BufferedReader(streamReader);
	}
}
