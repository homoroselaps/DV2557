package ai.impl;


import ai.impl.structure.Node;
import ai.impl.structure.Tree;




/**
 * Manages pruning.
 * Created by Nejc on 23. 09. 2016.
 */
public class PruningManager {


	private final Node node;


	public PruningManager(Node node) {
		this.node = node;
	}


	private static int getPreferredUtilityValue(int value1, int value2, boolean player) {
		if (player)
			return Math.max(value1, value2);
		else
			return Math.min(value1, value2);
	}






	// node stepped into
	public void onNodeProcessed() {

	}


	public PruningCallback onNodeChildCreated(Node child, int i) {
		if (node.getNextPlayer() == child.getNextPlayer()) {
			child.setUtilityValue(node.getUtilityValue()); // the child needs to exceed the threshold
			return null; // we shouldn't prune here
		}

		if (node.getNextPlayer() != child.getNextPlayer()) {
			// inject child checking
			return new PruningCallback() {
				@Override
				public boolean shouldPrune(Node grandChild) {
					if (node.getNextPlayer()) { // maximizer
						if (grandChild.getUtilityValue() < node.getUtilityValue())
							return true;
					} else { // minimizer
						if (grandChild.getUtilityValue() > node.getUtilityValue())
							return true;
					}
					return false;
				}
			};
		}

		return null;
	}


	public void onNodeChildProcessed(Node child, int i) {
		if (!node.hasUtilityValue()) { // first node
			node.setUtilityValue(child.getUtilityValue());
			return;
		}
		int nodeUtilityValue = node.getUtilityValue();
		int childUtilityValue = child.getUtilityValue();

		if (node.getNextPlayer()) {
			if (childUtilityValue > nodeUtilityValue)
				node.setUtilityValue(childUtilityValue);
		} else {
			if (childUtilityValue < nodeUtilityValue)
				node.setUtilityValue(childUtilityValue);
		}


	}


	// step function exit
	public void onNodeProcessedEnd(Tree tree) {
		// if there were no children, get the utility value from game state
		if (!node.hasUtilityValue())
			node.setUtilityValue(UtilityValueManager.getUtilityValueFromState(tree, node.getGameMove()));
	}








	public interface PruningCallback {


		boolean shouldPrune(Node grandChild);


	}

}
