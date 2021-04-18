package fr.sazaju.genshin.service.controller.coder;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.sazaju.genshin.simulator.wish.Settings;

class SettingsCoderTest implements CollectionCoderTest<Settings> {

	@ParameterizedTest
	@MethodSource("allCodersAndProfilesData")
	public void testEachVersionHasConsistentCoding(//
			SettingsCoder coder, //
			double probability4Stars, //
			double probability4StarsWeaponCharacter, //
			double probability5Stars, //
			double probability5StarsPermanentExclusive, //
			int guaranty4Stars, //
			int guaranty5Stars) throws IOException {

		Settings source = Settings.build()//
				.withProbability4Stars(probability4Stars)//
				.withProbability4StarsWeaponCharacter(probability4StarsWeaponCharacter)//
				.withProbability5Stars(probability5Stars)//
				.withProbability5StarsPermanentExclusive(probability5StarsPermanentExclusive)//
				.withGuaranty4Stars(guaranty4Stars)//
				.withGuaranty5Stars(guaranty5Stars)//
				.create();
		Settings rebuilt = coder.decode(coder.encode(source));
		assertEquals(probability4Stars, rebuilt.probability4Stars);
		assertEquals(probability4StarsWeaponCharacter, rebuilt.probability4StarsWeaponCharacter);
		assertEquals(probability5Stars, rebuilt.probability5Stars);
		assertEquals(probability5StarsPermanentExclusive, rebuilt.probability5StarsPermanentExclusive);
		assertEquals(guaranty4Stars, rebuilt.guaranty4Stars);
		assertEquals(guaranty5Stars, rebuilt.guaranty5Stars);
	}

	@Override
	public Coder<Settings, String> searchSerialCoder(String serial) {
		return SettingsCoder.fromSerial(serial);
	}

	@Override
	public Stream<Coder<Settings, String>> allCoders() {
		return Stream.of(SettingsCoder.values());
	}

	public Stream<Double> someProbabilityValues() {
		return Stream.of(0.0, 0.5, 1.0);
	}

	public Stream<Integer> someGuarantyValues() {
		return Stream.of(1, 123, Integer.MAX_VALUE);
	}

	@Override
	public Stream<Settings> allData() {
		return someProbabilityValues().flatMap(probability4Stars -> //
		someProbabilityValues().flatMap(probability5Stars -> {
			if (probability4Stars + probability5Stars > 1) {
				return Stream.empty();
			} else {
				return someProbabilityValues().flatMap(probability4StarsWeaponCharacter -> //
				someProbabilityValues().flatMap(probability5StarsPermanentExclusive -> //
				someGuarantyValues().flatMap(guaranty4Stars -> //
				someGuarantyValues().flatMap(guaranty5Stars -> {
					return Stream.of(Settings.build()//
							.withProbability4Stars(probability4Stars)//
							.withProbability4StarsWeaponCharacter(probability4StarsWeaponCharacter)//
							.withProbability5Stars(probability5Stars)//
							.withProbability5StarsPermanentExclusive(probability5StarsPermanentExclusive)//
							.withGuaranty4Stars(guaranty4Stars)//
							.withGuaranty5Stars(guaranty5Stars)//
							.create()//
					);
				}))));
			}
		}));
	}

	public Stream<Arguments> allCodersAndProfilesData() {
		return allCoders().flatMap(coder -> //
		someProbabilityValues().flatMap(probability4Stars -> //
		someProbabilityValues().flatMap(probability5Stars -> {
			if (probability4Stars + probability5Stars > 1) {
				return Stream.empty();
			} else {
				return someProbabilityValues().flatMap(probability4StarsWeaponCharacter -> //
				someProbabilityValues().flatMap(probability5StarsPermanentExclusive -> //
				someGuarantyValues().flatMap(guaranty4Stars -> //
				someGuarantyValues().flatMap(guaranty5Stars -> //
				Stream.of(arguments(//
						coder, //
						probability4Stars, //
						probability4StarsWeaponCharacter, //
						probability5Stars, //
						probability5StarsPermanentExclusive, //
						guaranty4Stars, //
						guaranty5Stars//
				))))));
			}
		})));
	}
}
