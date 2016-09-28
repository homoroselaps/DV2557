package ai.impl;


import ai.impl.structure.GameMove;
import ai.impl.structure.Node;
import ai.impl.structure.Tree;
import com.sun.media.jfxmedia.events.PlayerStateEvent;
import kalaha.GameState;

import java.util.List;




/**
 * GameUtility function and value provider.
 * Created by Nejc on 23. 09. 2016.
 */
public class UtilityValueManager {




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


	private final Tree tree;




	public Tree getTree() {
		return tree;
	}




	public UtilityValueManager(Tree tree) {
		this.tree = tree;
	}




//	public int getUtilityValue(GameMove gameMove, boolean previousPlayer) {
//		GameState gameState = gameMove.getGameState();
//
//		int player = tree.getPlayer(previousPlayer);
//		int opponent = GameUtility.getOpponent(player);
//
//		int valuePlayer = gameState.getScore(player);
//		int valueOpponent = gameState.getScore(opponent);
//
//		return valuePlayer - valueOpponent;
//	}


	public int getUtilityValue(GameMove gameMove) {
		GameState gameState = gameMove.getGameState();

		int valuePlayer = gameState.getScore(tree.getMaxPlayer());
		int valueOpponent = gameState.getScore(tree.getMinPlayer());

		return valuePlayer; // - valueOpponent;
	}


	/**
	 * Assigns a utility value to a {@link Node} based on current {@link GameState}.
	 *
	 * @param node The node
	 */
	public void assignFromGameState(Node node) {
//		int utilityValue = getUtilityValue(node.getGameMove(), node.getParent().getNextPlayer());
//		node.setUtilityValue(utilityValue);
		node.setUtilityValue(getUtilityValue(node.getGameMove()));
	}


	public static int getUtilityValueFromChildren(Node node) {
		List<Node> children = node.getChildren();

		if (children.size() == 0)
			return NO_VALUE;

		int value = children.get(0).getUtilityValue();
		MinMaxComparator comparator = getComparator(node.getNextPlayer());

		for (int i = 1; i < children.size(); i++) {
			value = comparator.get(value, children.get(i).getUtilityValue());
		}

		return value;
	}


	/**
	 * Sets {@link Node}'s utility value based on its children.
	 *
	 * @param node The node
	 * @return Whether the utility value has changed
	 */
	public boolean assignFromChildren(Node node) {
		int utilityValue = node.getUtilityValue();
		int newUtilityValue = getUtilityValueFromChildren(node);
		node.setUtilityValue(newUtilityValue);
		return utilityValue != newUtilityValue;
	}




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


	/**
	 * Checks if given player would prefer a proposed utility value over the currently existing utility value.
	 *
	 * @param currentUtilityValue  Currently existing utility value
	 * @param proposedUtilityValue Proposed utility value
	 * @param player               The player
	 * @return Whether or not the proposed value is preferred. Returns false with current and proposed values are equal.
	 */
	public static boolean isPreferred(int currentUtilityValue, int proposedUtilityValue, boolean player) {
		return player
				? proposedUtilityValue > currentUtilityValue
				: proposedUtilityValue < currentUtilityValue;
	}


	public static boolean isPreferred(Node node, int proposedUtilityValue) {
		return isPreferred(node.getUtilityValue(), proposedUtilityValue, node.getNextPlayer());
	}


	/**
	 * Updates {@link Node}'s ancestors' utility values.
	 *
	 * @param node Node which's utility value has changed.
	 */
	public static void updateNodeAncestorUtilityValues(Node node) {
		if (!node.hasParent())
			return; // we're finished

		Node parent = node.getParent();
		if (isPreferred(parent, node.getUtilityValue())) {
			// update the value
			parent.setUtilityValue(node.getUtilityValue());
			// find more ancestors
			updateNodeAncestorUtilityValues(parent);
		}
	}








	//@FunctionalInterface
	public interface MinMaxComparator {


		int get(int a, int b);


	}


}
