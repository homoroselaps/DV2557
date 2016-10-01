package ai.impl;


import ai.impl.structure.Node;
import com.sun.javafx.image.BytePixelSetter;

import javax.swing.text.AsyncBoxView;




/**
 * Manages pruning.
 * Created by Nejc on 23. 09. 2016.
 */
public final class PruningManager {


	private PruningManager() {
	}




	public static PruningCallback onNodeChildCreated(final Node node, Node child) {
		if (node.getNextPlayer() == child.getNextPlayer()) {
			child.setUtilityValue(node.getUtilityValue()); // the child needs to exceed the threshold
			return null; // we shouldn't prune here
		} else {
			// inject child checking
			return new PruningCallback() {
				@Override
				public boolean shouldPrune(Node grandChild) {
//					return false;
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


	public static void onNodeChildProcessed(Node node, Node child) {
		if (!node.hasUtilityValue()) { // first node
			node.setUtilityValue(child.getUtilityValue());
			node.setAmboToSelect(child.getGameMove().getSelectedAmbo());
			return;

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








	public interface PruningCallback {


		boolean shouldPrune(Node grandChild);


	}

}
