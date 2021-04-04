package fr.sazaju.genshin.csv;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

class CsvParserTest {
	
	// TODO Test wrong arguments
	// TODO Test invalid CSVs
	// TODO Test ignore empty lines

	@Test
	void testParseReturnsCorrectRows() throws IOException {
		// GIVEN
		String header1 = "header";
		String value1 = "X";
		String value2 = "Y";
		String value3 = "Z";

		CsvPreparator csv = new CsvPreparator();
		csv.println(header1);
		csv.println(value1);
		csv.println(value2);
		csv.println(value3);

		// WHEN
		List<String> rows = new CsvParser().parse(csv.toInputStream(), values -> {
			return values.get(header1);
		});

		// THEN
		assertEquals(List.of(value1, value2, value3), rows);
	}
	
	@Test
	void testIgnoreEmptyLines() throws IOException {
		// GIVEN
		String header1 = "header";
		String value1 = "X";
		String value2 = "Y";
		String value3 = "Z";

		CsvPreparator csv = new CsvPreparator();
		csv.println(header1);
		csv.println("");
		csv.println(value1);
		csv.println("");
		csv.println(value2);
		csv.println("");
		csv.println(value3);
		csv.println("");

		// WHEN
		List<String> rows = new CsvParser().parse(csv.toInputStream(), values -> {
			return values.get(header1);
		});

		// THEN
		assertEquals(List.of(value1, value2, value3), rows);
	}

	@Test
	void testValuesReturnCorrectRowValueForHeader() throws IOException {
		// GIVEN
		String header1 = "A";
		String header2 = "B";
		String header3 = "C";
		String value1 = "X";
		String value2 = "Y";
		String value3 = "Z";

		CsvPreparator csv = new CsvPreparator();
		csv.println(header1 + "," + header2 + "," + header3);
		csv.println(value1 + "," + value2 + "," + value3);

		// WHEN
		List<List<String>> rows = new CsvParser().parse(csv.toInputStream(), values -> {
			return List.of(//
					values.get(header3), //
					values.get(header1), //
					values.get(header1), //
					values.get(header2), //
					values.get(header3), //
					values.get(header2));
		});

		// THEN
		Iterator<String> iterator = rows.get(0).iterator();
		assertEquals(value3, iterator.next());
		assertEquals(value1, iterator.next());
		assertEquals(value1, iterator.next());
		assertEquals(value2, iterator.next());
		assertEquals(value3, iterator.next());
		assertEquals(value2, iterator.next());
	}

	static class CsvPreparator {
		private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		private final PrintStream csv = new PrintStream(outputStream);

		public void println(String line) {
			csv.println(line);
		}

		public InputStream toInputStream() {
			return new ByteArrayInputStream(outputStream.toByteArray());
		}
	}
}
