package ai.impl;


import ai.impl.util.Cancellable;
import ai.impl.util.CancellationTimer;
import kalaha.GameState;




/**
 * <p>Handles the AI logic; manages calls to other components.</p>
 * <p>Created by Nejc on 25. 09. 2016.</p>
 */
public class AIClientManager implements Cancellable {




	/**
	 * The default delay in milliseconds.
	 */
	public static final long DEFAULT_TIMEOUT = 4900;


	private final Node root;
	private final int maxPlayer;
	private final int minPlayer;
	private final CancellationTimer cancellationTimer;
	private final NodeBuilder nodeBuilder;
	private boolean running;
	private volatile boolean cancellationPending;




	/**
	 * Gets the root {@link Node}.
	 *
	 * @return The root node
	 */
	public Node getRoot() {
		return root;
	}


	/**
	 * Gets the AI player's code.
	 *
	 * @return The AI player code (1 ro 2)
	 */
	public int getMaxPlayer() {
		return maxPlayer;
	}


	/**
	 * Gets the opponent's code.
	 *
	 * @return The opponent's code (1 or 2)
	 */
	public int getMinPlayer() {
		return minPlayer;
	}


	/**
	 * Gets associated {@link NodeBuilder}.
	 *
	 * @return Associated {@link NodeBuilder}
	 */
	public NodeBuilder getNodeBuilder() {
		return nodeBuilder;
	}


	/**
	 * Gets next suggested move.
	 *
	 * @return Next suggested ambo index (1-6)
	 * @see NodeBuilder#getSelectedMove()
	 */
	public int getSelectedMove() {
		return nodeBuilder.getSelectedMove();
	}


	/**
	 * Check if this instance of {@link AIClientManager} is running.
	 *
	 * @return Whether or not the component is running
	 */
	public boolean isRunning() {
		return running;
	}


	/**
	 * Check if a cancellation has been requested.
	 *
	 * @return Whether or not a cancellation is pending
	 * @see Cancellable
	 */
	public boolean isCancellationPending() {
		return cancellationPending;
	}


	/**
	 * Cancels the search for suggested next move, if running.
	 *
	 * @see Cancellable
	 */
	public void cancel() {
		if (running)
			this.cancellationPending = true;
	}


	/**
	 * Gets the depth level that has been reached.
	 *
	 * @return Depth level reached
	 * @see NodeBuilder#getDepthReached()
	 */
	public int getDepthReached() {
		return nodeBuilder.getDepthReached();
	}




	/**
	 * Creates a new instance of {@link AIClientManager}
	 *
	 * @param root      Root node of the MiniMaxTree
	 * @param maxPlayer The {@link Integer} representation of the player on turn
	 * @param timeout   Execution timeout
	 */
	private AIClientManager(Node root, int maxPlayer, long timeout) {
		this.root = root;
		this.maxPlayer = maxPlayer;
		this.minPlayer = (maxPlayer % 2) + 1;
		this.nodeBuilder = new NodeBuilder(this);
		this.cancellationTimer = new CancellationTimer(nodeBuilder, timeout);
	}



	/**
	 * Creates a new instance of {@link AIClientManager}.
	 *
	 * @param gameState Game state from which the MiniMax tree should be created
	 * @param timeout   Maximum processing time
	 * @return A new instance of AIClientManager
	 */
	public static AIClientManager create(GameState gameState, long timeout) {
		GameMove gameMove = GameMove.create(gameState, true);
		Node node = new Node(gameMove, 0);
		return new AIClientManager(node, gameState.getNextPlayer(), timeout);
	}


	/**
	 * Creates a new instance of {@link AIClientManager}. with default timeout value.
	 *
	 * @param gameState Game state from which the MiniMax tree should be created
	 * @return A new instance of {@link AIClientManager}
	 */
	public static AIClientManager create(GameState gameState) {
		return create(gameState, DEFAULT_TIMEOUT);
	}




	/**
	 * Handles the logic before starting the search.
	 */
	private void start() {
		if (running)
			throw new IllegalStateException("Already running.");

		cancellationTimer.start();
		cancellationPending = false;
		running = true;
	}


	/**
	 * Handles the logic after the search has ended.
	 */
	private void end() {
		cancellationTimer.cancel();
		running = false;
		cancellationPending = false;
	}


	/**
	 * Starts the search for the best possible move.
	 *
	 * @param customDepthLevel Custom, pre-defined depth level
	 * @return Whether or not the action was processed without having been cancelled
	 */
	public boolean run(int customDepthLevel) {
		start();

		boolean res = nodeBuilder.run(customDepthLevel);

		end();
		return res;
	}


	/**
	 * Starts the iterative deepening search for the best possible move.
	 *
	 * @param depthLevelSupplier A provider of (relative) depth levels for the iterative deepening
	 * @return Whether or not the action was processed without having been cancelled
	 */
	public boolean run(DepthLevelSupplier depthLevelSupplier) {
		start();

		for (int depth : depthLevelSupplier) {
			boolean res = nodeBuilder.run(nodeBuilder.getDepthReached() + depth); // the levels are relative, therefore we need to add them to currently reached depth

			if (!res || cancellationPending) {
				end();
				return false;
			}
		}

		end();
		return true;
	}


}
