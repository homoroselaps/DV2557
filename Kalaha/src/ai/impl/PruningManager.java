package ai.impl;




/**
 * <p>Handles the logic for pruning.</p>
 * <p>Created by Nejc on 23. 09. 2016.</p>
 *
 * @see PruningCallback
 */
public final class PruningManager {


	/**
	 * Prevent instantiation.
	 */
	private PruningManager() {
	}




	/**
	 * Handles the logic of {@link Node}'s expansion after creating one of it's child nodes.
	 *
	 * @param node  The {@link Node} that is subject to expansion
	 * @param child The {@link Node}'s child that was just created
	 * @return Optional. {@link PruningCallback} to be invoked, when the child node's children are created
	 */
	public static PruningCallback onNodeChildCreated(final Node node, Node child) {
		if (node.getNextPlayer() == child.getNextPlayer()) {
			child.setUtilityValue(node.getUtilityValue()); // the child needs to exceed the threshold
			return null; // we shouldn't prune here
		} else if (node.hasUtilityValue()) {
			return null;
		} else {
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
	}


	/**
	 * Handles the logic of {@link Node}'s expansion after a {@link Node}'s child has been expanded.
	 *
	 * @param node  THe {@link Node} that is subject ot expansion
	 * @param child The {@link Node}'s child that was just expanded
	 */
	public static void onNodeChildProcessed(Node node, Node child) {
		if (node.hasUtilityValue()) { // first node
			node.setUtilityValue(child.getUtilityValue());
			node.setAmboToSelect(child.getGameMove().getSelectedAmbo());

		} else {
			int nodeUtilityValue = node.getUtilityValue();
			int childUtilityValue = child.getUtilityValue();

			if ((node.getNextPlayer() && childUtilityValue > nodeUtilityValue)
					|| (!node.getNextPlayer() && childUtilityValue < nodeUtilityValue)) {
				node.setUtilityValue(childUtilityValue);
				node.setAmboToSelect(child.getGameMove().getSelectedAmbo());
			}
		}
	}


}
