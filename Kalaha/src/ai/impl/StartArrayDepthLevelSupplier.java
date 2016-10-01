package ai.impl;


import java.util.Iterator;




/**
 * <p>Provides the caller with depth levels of iterative deepening.</p>
 * <p>Created by Nejc on 25. 09. 2016.</p>
 */
public class StartArrayDepthLevelSupplier implements DepthLevelSupplier {




	private final int[] startArray;
	private final int baseDepth;
	/**
	 * After the limit is reached, the supplier will not provide any more (relative) depth levels.
	 */
	private final int depthLimit;




	/**
	 * Gets the array of starting depth levels
	 *
	 * @return Array of starting depth levels
	 */
	public int[] getStartArray() {
		return startArray.clone();
	}


	/**
	 * Gets the base depth
	 *
	 * @return Base depth
	 */
	public int getBaseDepth() {
		return baseDepth;
	}


	/**
	 * Gets the maximum depth level limit
	 *
	 * @return Maximum depth level (inclusive)
	 */
	public int getDepthLimit() {
		return this.depthLimit;
	}


	/**
	 * Check if a maximum depth level limit exists
	 *
	 * @return Whether the maximum depth level limit exists
	 */
	public boolean hasMaxDepth() {
		return this.depthLimit != Integer.MAX_VALUE;
	}




	/**
	 * Creates an instance of this {@link StartArrayDepthLevelSupplier}.
	 *
	 * @param startArray An array of (relative) depth levels to be used as the starting (relative) depth levels of exploration in iterative deepening
	 * @param baseDepth  A (relative) depth level to be used for any further explorations in iterative deepening
	 * @param depthLimit Maximum depth to be reached (inclusive)
	 */
	public StartArrayDepthLevelSupplier(int[] startArray, int baseDepth, int depthLimit) {
		this.depthLimit = depthLimit;
		this.baseDepth = baseDepth;
		this.startArray = startArray;
	}




	/**
	 * Creates an instance of {@link StartArrayDepthLevelSupplier} with no depth limitation.
	 *
	 * @param baseDepth  A (relative) depth level to be used for any exploration in iterative deepening except for the first n moves
	 * @param startArray An array of (relative) depth levels ot be used as the starting (relative) depth levels of exploration in iterative deepening
	 * @return An instance of {@link StartArrayDepthLevelSupplier}
	 */
	public static StartArrayDepthLevelSupplier createNoLimit(int baseDepth, int... startArray) {
		return new StartArrayDepthLevelSupplier(startArray, baseDepth, Integer.MAX_VALUE);
	}


	/**
	 * Creates an instance of {@link StartArrayDepthLevelSupplier} with no depth limitation.
	 *
	 * @param depthLimit Maximum depth to be reached (inclusive)
	 * @param baseDepth  A (relative) depth level to be used for any exploration in iterative deepening except for the first n moves
	 * @param startArray An array of (relative) depth levels ot be used as the starting (relative) depth levels of exploration in iterative deepening
	 * @return An instance of {@link StartArrayDepthLevelSupplier}
	 */
	public static StartArrayDepthLevelSupplier create(int depthLimit, int baseDepth, int... startArray) {
		return new StartArrayDepthLevelSupplier(startArray, baseDepth, depthLimit);
	}




	/**
	 * Gets the {@link Iterator} that iterates through (relative) depth levels provided by this {@link StartArrayDepthLevelSupplier}.
	 *
	 * @return The {@link Iterator}
	 */
	@Override
	public Iterator<Integer> iterator() {
		return new Itr();
	}








	/**
	 * An {@link Iterator} that provides caller with (relative) depth levels defined by a {@link StartArrayDepthLevelSupplier}.
	 */
	private class Itr implements java.util.Iterator<Integer> {


		private int index;
		private int nextDepth;
		private int depthReached;


		/**
		 * Creates an instance of {@link Itr}
		 */
		public Itr() {
			moveNextLevel();
		}


		/**
		 * Performs logic to advance to the next (relative) depth level, if it exists.
		 */
		private void moveNextLevel() {
			if (this.index >= startArray.length) {
				nextDepth = baseDepth;
			} else {
				nextDepth = startArray[index];
				index++;
			}

			if (depthReached >= depthLimit) {
				nextDepth = 0;
			}
			depthReached += nextDepth;

			if (depthReached > depthLimit) {
				nextDepth -= depthReached - depthLimit;
			}
		}


		/**
		 * Unused. Cannot remove a (relative) depth level.
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException("Operation not supported.");
		}


		/**
		 * Checks if a next (relative) depth level is available.
		 *
		 * @return Whether a next (relative) depth level is available
		 */
		@Override
		public boolean hasNext() {
			return nextDepth > 0;
		}


		/**
		 * Returns the next (relative) depth level
		 *
		 * @return Next (relative) depth level, if it exists, otherwise 0
		 */
		@Override
		public Integer next() {
			int res = nextDepth;
			moveNextLevel();
			return res;
		}


	}


}
