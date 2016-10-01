package ai.impl;


import ai.impl.util.Cancellable;




/**
 * <p>Handles the logic of getting the best possible move.</p>
 * <p>Created by Nejc on 29. 09. 2016.</p>
 */
public class NodeBuilder implements Cancellable {




	private final AIClientManager clientManager;
	private int selectedMove = -1;
	private int depthReached;
	private boolean running;
	private volatile boolean cancellationPending;




	/**
	 * Gets associated {@link AIClientManager}.
	 *
	 * @return Associated {@link AIClientManager}
	 */
	public AIClientManager getClientManager() {
		return clientManager;
	}


	/**
	 * Gets the suggested move to make.
	 *
	 * @return Suggested move
	 */
	public int getSelectedMove() {
		return selectedMove;
	}


	/**
	 * Gets the amount of levels reached.
	 *
	 * @return Depth level reached
	 */
	public int getDepthReached() {
		return depthReached;
	}


	/**
	 * Check if the component is running.
	 *
	 * @return Whether or not the component is running
	 */
	public boolean isRunning() {
		return running;
	}


	/**
	 * Checks if a cancellation has been requested.
	 *
	 * @return Whether or not a cancellation is pending
	 * @see Cancellable
	 */
	@Override
	public boolean isCancellationPending() {
		return cancellationPending;
	}


	/**
	 * Cancels the search, if running.
	 *
	 * @see Cancellable
	 */
	@Override
	public void cancel() {
		if (running)
			this.cancellationPending = true;
	}




	/**
	 * Creates an instance of this {@link NodeBuilder}.
	 *
	 * @param clientManager Associated {@link AIClientManager}.
	 */
	public NodeBuilder(AIClientManager clientManager) {
		this.clientManager = clientManager;
	}




	/**
	 * Start the search.
	 *
	 * @param depth Desired depth to reach.
	 * @return Whether or not the operation has ended without being cancelled.
	 */
	public boolean run(int depth) {
		if (depth <= depthReached)
			return true;

		if (running)
			throw new IllegalStateException("Already running.");
		running = true;
		cancellationPending = false;

		Node root = clientManager.getRoot();
		root.clearUtilityValue();
		root.setLevelsToAdd(depth);

		step(root, null);

		if (!cancellationPending) {
			depthReached = depth;
			selectedMove = root.getAmboToSelect();
		}

		running = false;
		return !cancellationPending;
	}


	/**
	 * Handles the logic of expanding a {@link Node}. (Recursively called - implements depth-first expansion of the tree.)
	 *
	 * @param node            {@link Node} to expand
	 * @param pruningCallback Optional. The {@link PruningCallback} to check for pruning.
	 */
	private void step(Node node, PruningCallback pruningCallback) {

		if (node.isLeaf()) {
			node.setUtilityValue(UtilityValueManager.getUtilityValueFromState(clientManager, node.getGameMove()));
			return;
		}

		for (GameMove gameMove : node.getGameMove().getGameMoveProvider()) {
			Node child = node.createChild(gameMove);
			PruningCallback pc = PruningManager.onNodeChildCreated(node, child);

			if (cancellationPending)
				break;

			step(child, pc);
			PruningManager.onNodeChildProcessed(node, child);

			if (pruningCallback != null && pruningCallback.shouldPrune(child))
				break;

		}

	}


}
