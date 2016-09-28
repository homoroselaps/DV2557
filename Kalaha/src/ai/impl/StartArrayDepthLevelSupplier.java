package ai.impl;


import java.util.Iterator;




/**
 * Allows supplying with depth levels.
 * Created by Nejc on 25. 09. 2016.
 */
public class StartArrayDepthLevelSupplier implements DepthLevelSupplier {




	private final int[] startArray;
	private final int baseDepth;
	/**
	 * After the limit is reached, the supplier will not provide any more depth levels.
	 */
	private final int depthLimit;




	public int[] getStartArray() {
		return startArray.clone();
	}


	public int getBaseDepth() {
		return baseDepth;
	}


	public int getDepthLimit() {
		return this.depthLimit;
	}


	public boolean hasMaxDepth() {
		return this.depthLimit != Integer.MAX_VALUE;
	}




	public StartArrayDepthLevelSupplier(int[] startArray, int baseDepth, int depthLimit) {
		this.depthLimit = depthLimit;
		this.baseDepth = baseDepth;
		this.startArray = startArray;
	}




	public static StartArrayDepthLevelSupplier createNoLimit(int baseDepth, int... startArray) {
		return new StartArrayDepthLevelSupplier(startArray, baseDepth, Integer.MAX_VALUE);
	}


	public static StartArrayDepthLevelSupplier create(int depthLimit, int baseDepth, int... startArray) {
		return new StartArrayDepthLevelSupplier(startArray, baseDepth, depthLimit);
	}




	@Override
	public Iterator<Integer> iterator() {
		return new Itr();
	}








	private class Itr implements java.util.Iterator<Integer> {


		private int index;
		private int nextDepth;
		private int depthReached;


		public Itr() {
			moveNextLevel();
		}


		private void moveNextLevel() {
			StartArrayDepthLevelSupplier base = StartArrayDepthLevelSupplier.this;

			if (this.index >= base.startArray.length) {
				nextDepth = base.baseDepth;
			} else {
				nextDepth = base.startArray[index];
				index++;
			}


			if (depthReached >= base.depthLimit) {
				nextDepth = 0;
			}
			depthReached += nextDepth;
		}


		@Override
		public void remove() {
			throw new UnsupportedOperationException("Action not supported.");
		}


		@Override
		public boolean hasNext() {
			return this.nextDepth > 0;
		}


		@Override
		public Integer next() {
			int res = this.nextDepth;
			moveNextLevel();
			return res;
		}


	}


}
