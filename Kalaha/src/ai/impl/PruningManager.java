package ai.impl;


import ai.impl.structure.Node;




/**
 * Manages pruning.
 * Created by Nejc on 23. 09. 2016.
 */
public class PruningManager {




	private int alpha;
	private int beta;




	public int getAlpha() {
		return alpha;
	}


	public int getBeta() {
		return beta;
	}




	public PruningManager() {
		alpha = 0;
		beta = Integer.MAX_VALUE;
	}




	/**
	 * Updates associated data and checks if pruning can be done.
	 *
	 * @param value The Maximizer's utility value
	 * @return Whether or not to prune
	 */
	public boolean resolveMaximizerValue(int value) {
		// "If the retrieved value is higher than Alpha, we update the Alpha."
		if (value > alpha)
			alpha = value;

		// "If the current value is higher than Beta, we prune!"
		return value > beta;
	}


	/**
	 * Updates associated data and checks if pruning can be done.
	 *
	 * @param value The Minimizer's utility value
	 * @return Whether or not to prune
	 */
	public boolean resolveMinimizerValue(int value) {
		// If the retrieved value is lower than Beta, we update the Beta.
		if (value < beta)
			beta = value;

		// If the current value is lower than Alpha, we prune!
		return value < alpha;
	}


	public boolean resolveNodeValue(Node node) {
		if (node.getParent().getGameMove().getNextPlayer())
			return resolveMaximizerValue(node.getUtilityValue()); // parent is a maximizer
		else
			return resolveMinimizerValue(node.getUtilityValue()); // parent is a minimizer
	}


}
