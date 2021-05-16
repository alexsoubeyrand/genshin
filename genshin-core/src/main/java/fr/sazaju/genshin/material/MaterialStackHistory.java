package fr.sazaju.genshin.material;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface MaterialStackHistory {

	MaterialStack getInitialStack();

	MaterialStack getResultingStack();

	MaterialStack getResultingDiff();

	Stream<MaterialStack> streamStacks();

	Stream<MaterialStack> streamDiffs();

	public static MaterialStackHistory from(MaterialStack stack) {
		return new MaterialStackHistory() {
			@Override
			public MaterialStack getInitialStack() {
				return stack;
			}

			@Override
			public MaterialStack getResultingStack() {
				return stack;
			}

			@Override
			public MaterialStack getResultingDiff() {
				return MaterialStack.empty();
			}

			@Override
			public Stream<MaterialStack> streamStacks() {
				return Stream.of(stack);
			}

			@Override
			public Stream<MaterialStack> streamDiffs() {
				return Stream.empty();
			}

			@Override
			public boolean equals(Object obj) {
				return Internal.areEqual(this, obj);
			}

			@Override
			public int hashCode() {
				return Internal.hashCode(this);
			}

		};
	}

	default MaterialStackHistory appendStack(MaterialStack stack) {
		MaterialStackHistory history = this;
		return new MaterialStackHistory() {
			@Override
			public MaterialStack getInitialStack() {
				return history.getInitialStack();
			}

			private MaterialStack getDiff() {
				return stack.minusStack(history.getResultingStack());
			}

			@Override
			public MaterialStack getResultingStack() {
				return stack;
			}

			@Override
			public MaterialStack getResultingDiff() {
				return stack.minusStack(history.getInitialStack());
			}

			@Override
			public Stream<MaterialStack> streamStacks() {
				return Stream.concat(history.streamStacks(), Stream.of(getResultingStack()));
			}

			@Override
			public Stream<MaterialStack> streamDiffs() {
				return Stream.concat(history.streamDiffs(), Stream.of(getDiff()));
			}

			@Override
			public boolean equals(Object obj) {
				return Internal.areEqual(this, obj);
			}

			@Override
			public int hashCode() {
				return Internal.hashCode(this);
			}
		};
	}

	default MaterialStackHistory appendDiff(MaterialStack diff) {
		MaterialStackHistory history = this;
		return new MaterialStackHistory() {
			@Override
			public MaterialStack getInitialStack() {
				return history.getInitialStack();
			}

			@Override
			public MaterialStack getResultingStack() {
				return history.getResultingStack().addStack(diff);
			}

			@Override
			public MaterialStack getResultingDiff() {
				return history.getResultingDiff().addStack(diff);
			}

			@Override
			public Stream<MaterialStack> streamStacks() {
				return Stream.concat(history.streamStacks(), Stream.of(getResultingStack()));
			}

			@Override
			public Stream<MaterialStack> streamDiffs() {
				return Stream.concat(history.streamDiffs(), Stream.of(diff));
			}

			@Override
			public boolean equals(Object obj) {
				return Internal.areEqual(this, obj);
			}

			@Override
			public int hashCode() {
				return Internal.hashCode(this);
			}
		};
	}

	public static class Internal {
		private static List<MaterialStack> collect(Stream<MaterialStack> stream) {
			return stream.collect(Collectors.toList());
		}

		private static boolean areEqual(MaterialStackHistory history, Object obj) {
			if (obj == history) {
				return true;
			} else if (obj instanceof MaterialStackHistory) {
				MaterialStackHistory that = (MaterialStackHistory) obj;
				return Objects.equals(history.getResultingStack(), that.getResultingStack())//
						&& Objects.equals(collect(history.streamDiffs()), collect(that.streamDiffs()));
			} else {
				return false;
			}
		}

		private static int hashCode(MaterialStackHistory history) {
			return Objects.hash(history.getResultingStack(), collect(history.streamDiffs()));
		}
	}
}
