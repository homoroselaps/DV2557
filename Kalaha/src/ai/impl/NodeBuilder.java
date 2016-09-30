package ai.impl;


import ai.impl.structure.GameMove;
import ai.impl.structure.Node;
import ai.impl.util.Cancellable;

import java.util.Iterator;




/**
 * ??
 * Created by Nejc on 29. 09. 2016.
 */
public class NodeBuilder implements Cancellable {




	private AIClientManager clientManager;
	private int selectedMove = -1;
	private int depthReached;
	private boolean running;
	private volatile boolean cancellationPending;
	private int stepCount;




	public AIClientManager getClientManager() {
		return clientManager;
	}


	public int getSelectedMove() {
		return selectedMove;
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




	public NodeBuilder(AIClientManager clientManager) {
		this.clientManager = clientManager;
	}




	public boolean run(int depth) {
		if (depth <= depthReached)
			return true;

		if (running)
			throw new IllegalStateException("Already running.");
		running = true;
		cancellationPending = false;

		stepCount = 0;
		Node root = clientManager.getRoot();
		root.setLevelsToAdd(depth);

		selectedMove = step(root, null);

		if (!cancellationPending)
			depthReached = depth;

		running = false;
		return !cancellationPending;
	}


	private int step(Node node, PruningManager.PruningCallback pruningCallback) {
		if (cancellationPending)
			return -1;

		int sc = stepCount;
		stepCount++;

		if (node.isLeaf()) {
			node.setUtilityValue(UtilityValueManager.getUtilityValueFromState(clientManager, node.getGameMove()));
			return node.getGameMove().getSelectedAmbo();
		}

		// the node is not a leaf
		node.clearUtilityValue();

		Iterator<GameMove> gameMoveIterator = node.getGameMove().getGameMoveProvider().iterator();

		int i = -1;
		while (gameMoveIterator.hasNext()) {
			GameMove gameMove = gameMoveIterator.next();
			i++;

			Node child = node.createChild(gameMove);
			PruningManager.PruningCallback pc = PruningManager.onNodeChildCreated(node, child);
			step(child, pc);
			PruningManager.onNodeChildProcessed(node, child);

			if (pruningCallback != null && pruningCallback.shouldPrune(child))
				break; // prune

		}

		return node.amboToSelect;

	}


}
