package ai.impl;


import java.util.Iterator;




/**
 * Allows supplying with depth levels.
 * Created by Nejc on 25. 09. 2016.
 */
public class StartArrayDepthLevelSupplier implements DepthLevelSupplier {




	private final int[] startArray;
	private final int baseDepth;




	public int[] getStartArray() {
		return startArray.clone();
	}


	public int getBaseDepth() {
		return baseDepth;
	}




	public StartArrayDepthLevelSupplier(int baseDepth, int... startArray) {
		this.startArray = startArray;
		this.baseDepth = baseDepth;
	}




	@Override
	public Iterator<Integer> iterator() {
		return new Itr();
	}








	private class Itr implements java.util.Iterator<Integer> {


		private int index;


		@Override
		public void remove() {
			throw new UnsupportedOperationException("Action not supported.");
		}


		@Override
		public boolean hasNext() {
			return true; // yes, there are infinite elements here
		}


		@Override
		public Integer next() {
			StartArrayDepthLevelSupplier _super = StartArrayDepthLevelSupplier.this;

			int res;
			if (index >= 0 && index < _super.startArray.length)
				res = _super.startArray[index];
			else
				res = _super.baseDepth;

			index++;

			return res;
		}


	}


}
