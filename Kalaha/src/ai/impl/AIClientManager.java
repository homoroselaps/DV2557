package ai.impl;


import ai.impl.structure.GameMove;
import ai.impl.structure.Node;
import ai.impl.util.Cancellable;
import ai.impl.util.CancellationTimer;
import kalaha.GameState;




/**
 * Manages the calls to sub-components.
 * Created by Nejc on 25. 09. 2016.
 */
public class AIClientManager implements Cancellable {




	/**
	 * Timeout delay in milliseconds.
	 */
	public static final long DEFAULT_TIMEOUT = 4900;


	private final Node root;
	private final int maxPlayer;
	private final int minPlayer;
	private final CancellationTimer cancellationTimer;
	private final NodeBuilder nodeBuilder;
	private int selectedMove;
	private boolean running;
	private volatile boolean cancellationPending;




	public Node getRoot() {
		return root;
	}


	public int getMaxPlayer() {
		return maxPlayer;
	}


	public int getMinPlayer() {
		return minPlayer;
	}


	public NodeBuilder getNodeBuilder() {
		return nodeBuilder;
	}


	public int getSelectedMove() {
		return selectedMove;
	}


	public boolean isRunning() {
		return running;
	}


	public boolean isCancellationPending() {
		return cancellationPending;
	}


	public void cancel() {
		if (running)
			this.cancellationPending = true;
	}


	public int getDepthReached() {
		return nodeBuilder.getDepthReached();
	}




	private AIClientManager(Node root, int maxPlayer, long timeout) {
		this.root = root;
		this.maxPlayer = maxPlayer;
		this.minPlayer = (maxPlayer % 2) + 1;
		this.nodeBuilder = new NodeBuilder(this);
		this.cancellationTimer = new CancellationTimer(nodeBuilder, timeout);
	}




	public static AIClientManager create(GameState gameState, long timeout) {
		GameMove gameMove = GameMove.create(gameState, true);
		Node node = new Node(gameMove, 0);
		return new AIClientManager(node, 2, timeout);
	}


	public static AIClientManager create(GameState gameState) {
		return create(gameState, DEFAULT_TIMEOUT);
	}




	private void start() {
		if (running)
			throw new IllegalStateException("Already running.");

		cancellationTimer.start();
		cancellationPending = false;
		running = true;
	}


	private void end() {
		cancellationTimer.cancel();
		running = false;
		cancellationPending = false;
	}


	public boolean run(int customDepthLevel) {
		start();

		boolean res = nodeBuilder.run(customDepthLevel);

		end();
		return res;
	}


	public boolean run(DepthLevelSupplier depthLevelSupplier) {
		start();

		for (int depth : depthLevelSupplier) {
			boolean res = nodeBuilder.run(nodeBuilder.getDepthReached() + depth);

			if (!res) {
				end();
				return false;
			} else {
				this.selectedMove = nodeBuilder.getSelectedMove();
			}
		}

		end();
		return true;
	}


}
