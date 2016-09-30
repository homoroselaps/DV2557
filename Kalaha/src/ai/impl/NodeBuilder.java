package ai.impl;


import ai.impl.structure.GameMove;
import ai.impl.structure.Node;
import ai.impl.structure.Tree;
import ai.impl.util.Cancellable;

import java.util.Iterator;
import java.util.List;




/**
 * ??
 * Created by Nejc on 29. 09. 2016.
 */
public class NodeBuilder implements Cancellable {




	private final Tree tree;
	private int depthReached;
	private boolean running;
	private boolean cancellationPending;


	public Tree getTree() {
		return tree;
	}


	public int getDepthReached() {
		return depthReached;
	}


	public boolean isRunning() {
		return running;
	}


	@Override
	public boolean isCancellationPending() {
		return cancellationPending;
	}


	public void cancel() {
		if (running)
			this.cancellationPending = true;
	}




	public NodeBuilder(Tree tree) {
		this.tree = tree;
	}




	public boolean run(int depth) {
		if (depth <= depthReached)
			return true;
		if (running)
			throw new IllegalStateException("Already running.");
		running = true;
		cancellationPending = false;

		Node root = tree.getRoot();
		root.setLevelsToAdd(depth);
		step(root, null);

		if (!cancellationPending)
			depthReached = depth;

		running = false;
		return !cancellationPending;
	}




	private void step(Node node, PruningManager.PruningCallback pruningCallback) {

		if (node.isLeaf()) {
			// the node is a leaf
			node.setUtilityValue(UtilityValueManager.getUtilityValueFromState(tree, node.getGameMove()));
			return;
		}

		// the node is not a leaf

		node.clearUtilityValue();

		List<Node> children = node.getChildren();
		int childrenCount = children.size();
		Iterator<GameMove> gameMoveIterator = node.getGameMove().getGameMoveProvider().iterator();
		PruningManager pruningManager = new PruningManager(node);

		pruningManager.onNodeProcessed();

		int i = -1;
		while (gameMoveIterator.hasNext()) {
			GameMove gameMove = gameMoveIterator.next();
			i++;

			// create or assign child
			Node child;
			if (i < childrenCount) {
				child = children.get(i); // an item with this move is already contained in the children list
				child.setLevelsToAdd(node.getLevelsToAdd() - 1);
				child.clearUtilityValue();
			} else {
				child = node.createChild(gameMove);
			}
			// a child MUST NOT HAVE a utility value at this point,
			// otherwise, pruning might fail

			PruningManager.PruningCallback pc = pruningManager.onNodeChildCreated(child, i);

			step(child, pc); // recursive call

			pruningManager.onNodeChildProcessed(child, i);

			if (pruningCallback != null && pruningCallback.shouldPrune(child)) {
				// PRUNE!
				break;
			}


		} // end while

		pruningManager.onNodeProcessedEnd(tree);


	}


}
