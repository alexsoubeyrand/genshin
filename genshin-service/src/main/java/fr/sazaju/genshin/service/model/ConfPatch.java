package fr.sazaju.genshin.service.model;

import java.util.List;

import fr.sazaju.genshin.service.controller.coder.ConfigurationDefinition.Configuration;
import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.FixedNGDescriptor;
import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.ListNGDescriptor;
import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.NumberGeneratorDescriptor;
import fr.sazaju.genshin.service.controller.coder.NumberGeneratorDescriptorDefinition.RandomNGDescriptor;
import fr.sazaju.genshin.simulator.wish.Settings;
import fr.sazaju.genshin.simulator.wish.State;

public class ConfPatch implements Modifier<Configuration> {
	enum SettingsKey {
		MIHOYO, UNLUCKY
	}

	private SettingsKey settingsKey;
	private Double probability4Stars;
	private Double probability4StarsWeaponCharacter;
	private Double probability5Stars;
	private Double probability5StarsPermanentExclusive;
	private Integer guaranty4Stars;
	private Integer guaranty5Stars;

	enum StateKey {
		FRESH
	}

	private StateKey stateKey;
	private Integer consecutiveWishesBelow4Stars;
	private Integer consecutiveWishesBelow5Stars;
	private Boolean isExclusiveGuaranteedOnNext5Stars;

	private Float randomValue;
	private List<Float> randomList;
	private Integer randomListOffset;
	private Long randomSeed;

	public SettingsKey getSettingsKey() {
		return settingsKey;
	}

	public void setSettingsKey(SettingsKey settingsKey) {
		this.settingsKey = settingsKey;
	}

	public StateKey getStateKey() {
		return stateKey;
	}

	public void setStateKey(StateKey stateKey) {
		this.stateKey = stateKey;
	}

	public Long getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(Long randomSeed) {
		this.randomSeed = randomSeed;
	}

	public Float getRandomValue() {
		return randomValue;
	}

	public void setRandomValue(Float fixedRandom) {
		this.randomValue = fixedRandom;
	}

	public Integer getGuaranty4Stars() {
		return guaranty4Stars;
	}

	public void setGuaranty4Stars(Integer guaranty4Stars) {
		this.guaranty4Stars = guaranty4Stars;
	}

	public Integer getGuaranty5Stars() {
		return guaranty5Stars;
	}

	public void setGuaranty5Stars(Integer guaranty5Stars) {
		this.guaranty5Stars = guaranty5Stars;
	}

	public Double getProbability4Stars() {
		return probability4Stars;
	}

	public void setProbability4Stars(Double probability4Stars) {
		this.probability4Stars = probability4Stars;
	}

	public Double getProbability4StarsWeaponCharacter() {
		return probability4StarsWeaponCharacter;
	}

	public void setProbability4StarsWeaponCharacter(Double probability4StarsWeaponCharacter) {
		this.probability4StarsWeaponCharacter = probability4StarsWeaponCharacter;
	}

	public Double getProbability5Stars() {
		return probability5Stars;
	}

	public void setProbability5Stars(Double probability5Stars) {
		this.probability5Stars = probability5Stars;
	}

	public Double getProbability5StarsPermanentExclusive() {
		return probability5StarsPermanentExclusive;
	}

	public void setProbability5StarsPermanentExclusive(Double probability5StarsPermanentExclusive) {
		this.probability5StarsPermanentExclusive = probability5StarsPermanentExclusive;
	}

	public List<Float> getRandomList() {
		return randomList;
	}

	public void setRandomList(List<Float> randomList) {
		this.randomList = randomList;
	}

	public Integer getRandomListOffset() {
		return randomListOffset;
	}

	public void setRandomListOffset(Integer randomListOffset) {
		this.randomListOffset = randomListOffset;
	}

	public Integer getConsecutiveWishesBelow4Stars() {
		return consecutiveWishesBelow4Stars;
	}

	public void setConsecutiveWishesBelow4Stars(Integer consecutiveWishesBelow4Stars) {
		this.consecutiveWishesBelow4Stars = consecutiveWishesBelow4Stars;
	}

	public Integer getConsecutiveWishesBelow5Stars() {
		return consecutiveWishesBelow5Stars;
	}

	public void setConsecutiveWishesBelow5Stars(Integer consecutiveWishesBelow5Stars) {
		this.consecutiveWishesBelow5Stars = consecutiveWishesBelow5Stars;
	}

	public Boolean getIsExclusiveGuaranteedOnNext5Stars() {
		return isExclusiveGuaranteedOnNext5Stars;
	}

	public void setIsExclusiveGuaranteedOnNext5Stars(Boolean isExclusiveGuaranteedOnNext5Stars) {
		this.isExclusiveGuaranteedOnNext5Stars = isExclusiveGuaranteedOnNext5Stars;
	}

	@Override
	public Configuration apply(Configuration source) {
		return new Configuration(//
				updateSettings(source.settings), //
				updateState(source.state), //
				updateDescriptor(source.numberGeneratorDescriptor)//
		);
	}

	private NumberGeneratorDescriptor<?> updateDescriptor(NumberGeneratorDescriptor<?> source) {
		if (randomValue != null) {
			return new FixedNGDescriptor(randomValue);
		} else if (randomList != null) {
			if (randomListOffset != null) {
				return new ListNGDescriptor(randomList, randomListOffset);
			} else {
				return new ListNGDescriptor(randomList, 0);
			}
		} else if (randomSeed != null) {
			return new RandomNGDescriptor(randomSeed);
		} else {
			return source;
		}
	}

	private State updateState(State source) {
		if (stateKey == StateKey.FRESH) {
			source = State.createFresh();
		} else {
			// Keep current source
		}
		return new State(//
				update(source.consecutiveWishesBelow4Stars, this.consecutiveWishesBelow4Stars), //
				update(source.consecutiveWishesBelow5Stars, this.consecutiveWishesBelow5Stars), //
				update(source.isExclusiveGuaranteedOnNext5Stars, this.isExclusiveGuaranteedOnNext5Stars)//
		);
	}

	private Settings updateSettings(Settings source) {
		if (settingsKey == SettingsKey.MIHOYO) {
			source = Settings.createMihoyoSettings();
		} else if (settingsKey == SettingsKey.UNLUCKY) {
			source = Settings.build()//
					.withProbability4Stars(0)//
					.withProbability4StarsWeaponCharacter(0)//
					.withProbability5Stars(0)//
					.withProbability5StarsPermanentExclusive(0)//
					.withGuaranty4Stars(Integer.MAX_VALUE)//
					.withGuaranty5Stars(Integer.MAX_VALUE)//
					.create();
		} else {
			// Keep current source
		}
		try {
			return Settings.build()//
					.withProbability4Stars(update(source.probability4Stars, this.probability4Stars))//
					.withProbability4StarsWeaponCharacter(
							update(source.probability4StarsWeaponCharacter, this.probability4StarsWeaponCharacter))//
					.withProbability5Stars(update(source.probability5Stars, this.probability5Stars))//
					.withProbability5StarsPermanentExclusive(update(source.probability5StarsPermanentExclusive,
							this.probability5StarsPermanentExclusive))//
					.withGuaranty4Stars(update(source.guaranty4Stars, this.guaranty4Stars))//
					.withGuaranty5Stars(update(source.guaranty5Stars, this.guaranty5Stars))//
					.create();
		} catch (IllegalArgumentException | IllegalStateException cause) {
			throw new InvalidModifierException(cause);
		}
	}

	private <T> T update(T source, T update) {
		return update == null ? source : update;
	}

}
