package ai.impl;


import ai.impl.structure.GameMove;
import kalaha.GameState;




/**
 * GameUtility function and value provider.
 * Created by Nejc on 23. 09. 2016.
 */
public final class UtilityValueManager {


	private UtilityValueManager() {
	}




	public static final int NO_VALUE = Integer.MIN_VALUE;
	private static final MinMaxComparator MIN_COMPARATOR = new MinMaxComparator() {
		@Override
		public int get(int a, int b) {
			return Math.min(a, b);
		}
	};
	private static final MinMaxComparator MAX_COMPARATOR = new MinMaxComparator() {
		@Override
		public int get(int a, int b) {
			return Math.max(a, b);
		}
	};







	public static int getUtilityValueFromState(AIClientManager clientManager, GameMove gameMove) {
		GameState gameState = gameMove.getGameState();

		int valuePlayer = gameState.getScore(clientManager.getMaxPlayer());
		int valueOpponent = gameState.getScore(clientManager.getMinPlayer());

		return valuePlayer; // - valueOpponent;
	}


//	public static int getUtilityValueFromChildren(Node node) {
//		List<Node> children = node.getChildren();
//
//		if (children.size() == 0)
//			return NO_VALUE;
//
//		int value = children.get(0).getUtilityValue();
//		MinMaxComparator comparator = getComparator(node.getNextPlayer());
//
//		for (int i = 1; i < children.size(); i++) {
//			value = comparator.get(value, children.get(i).getUtilityValue());
//		}
//
//		return value;
//	}





	public static boolean hasValue(int value) {
		return value != NO_VALUE;
	}


	public static MinMaxComparator getComparator(boolean player) {
//		return player
//				? Math::max
//				: Math::min;
		return player
				? MAX_COMPARATOR
				: MIN_COMPARATOR;
	}








	//@FunctionalInterface
	public interface MinMaxComparator {


		int get(int a, int b);


	}


}
