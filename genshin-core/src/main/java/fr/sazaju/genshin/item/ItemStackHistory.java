package fr.sazaju.genshin.item;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO Use Recipe for diffs
public interface ItemStackHistory {

	ItemStack getInitialStack();

	ItemStack getResultingStack();

	ItemStack getResultingDiff();

	Stream<ItemStack> streamStacks();

	Stream<ItemStack> streamDiffs();

	public static ItemStackHistory from(ItemStack stack) {
		return new ItemStackHistory() {
			@Override
			public ItemStack getInitialStack() {
				return stack;
			}

			@Override
			public ItemStack getResultingStack() {
				return stack;
			}

			@Override
			public ItemStack getResultingDiff() {
				return ItemStack.empty();
			}

			@Override
			public Stream<ItemStack> streamStacks() {
				return Stream.of(stack);
			}

			@Override
			public Stream<ItemStack> streamDiffs() {
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

	default ItemStackHistory appendStack(ItemStack stack) {
		ItemStackHistory history = this;
		return new ItemStackHistory() {
			@Override
			public ItemStack getInitialStack() {
				return history.getInitialStack();
			}

			private ItemStack getDiff() {
				return stack.minusStack(history.getResultingStack());
			}

			@Override
			public ItemStack getResultingStack() {
				return stack;
			}

			@Override
			public ItemStack getResultingDiff() {
				return stack.minusStack(history.getInitialStack());
			}

			@Override
			public Stream<ItemStack> streamStacks() {
				return Stream.concat(history.streamStacks(), Stream.of(getResultingStack()));
			}

			@Override
			public Stream<ItemStack> streamDiffs() {
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

	default ItemStackHistory appendDiff(ItemStack diff) {
		ItemStackHistory history = this;
		return new ItemStackHistory() {
			@Override
			public ItemStack getInitialStack() {
				return history.getInitialStack();
			}

			@Override
			public ItemStack getResultingStack() {
				return history.getResultingStack().addStack(diff);
			}

			@Override
			public ItemStack getResultingDiff() {
				return history.getResultingDiff().addStack(diff);
			}

			@Override
			public Stream<ItemStack> streamStacks() {
				return Stream.concat(history.streamStacks(), Stream.of(getResultingStack()));
			}

			@Override
			public Stream<ItemStack> streamDiffs() {
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
		private static List<ItemStack> collect(Stream<ItemStack> stream) {
			return stream.collect(Collectors.toList());
		}

		private static boolean areEqual(ItemStackHistory history, Object obj) {
			if (obj == history) {
				return true;
			} else if (obj instanceof ItemStackHistory) {
				ItemStackHistory that = (ItemStackHistory) obj;
				return Objects.equals(history.getResultingStack(), that.getResultingStack())//
						&& Objects.equals(collect(history.streamDiffs()), collect(that.streamDiffs()));
			} else {
				return false;
			}
		}

		private static int hashCode(ItemStackHistory history) {
			return Objects.hash(history.getResultingStack(), collect(history.streamDiffs()));
		}
	}
}
