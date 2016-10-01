package ai.impl;




/**
 * <p>Describes a pruning callback to check, if pruning is requested.</p>
 * <p>Created by Nejc on 1. 10. 2016.</p>
 *
 * @see PruningManager
 * @see PruningManager#onNodeChildCreated(Node, Node)
 */
// @FunctionalInterface
public interface PruningCallback {


	/**
	 * Checks if pruning is requested.
	 *
	 * @param grandChild {@link Node} which's expansion might have resulted in pruning
	 * @return Whether or not to prune (skip other grandChild's siblings)
	 */
	boolean shouldPrune(Node grandChild);


}
