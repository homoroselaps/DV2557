package ai.impl;


import ai.impl.structure.Tree;
import ai.impl.util.Cancellable;
import ai.impl.util.CancellationTimer;




/**
 * Manages the calls to sub-components.
 * Created by Nejc on 25. 09. 2016.
 */
public class AIClientManager implements Cancellable {




	/**
	 * Timeout delay in milliseconds.
	 */
	public static final long DEFAULT_TIMEOUT = 4900;


	private final CancellationTimer cancellationTimer;
	private final NodeBuilder nodeBuilder;
	private boolean running;
	private boolean cancellationPending;




	public NodeBuilder getNodeBuilder() {
		return nodeBuilder;
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




	public AIClientManager(NodeBuilder nodeBuilder, long timeout) {
		this.nodeBuilder = nodeBuilder;
		this.cancellationTimer = new CancellationTimer(nodeBuilder, timeout);
	}


	public AIClientManager(NodeBuilder nodeBuilder) {
		this(nodeBuilder, DEFAULT_TIMEOUT);
	}




	public static AIClientManager fromTree(Tree tree) {
		return fromTree(tree, DEFAULT_TIMEOUT);
	}


	public static AIClientManager fromTree(Tree tree, long timeout) {
		return new AIClientManager(new NodeBuilder(tree), timeout);
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

			if (this.cancellationPending || !res) {
				end();
				return false;
			}
		}

		end();
		return true;
	}


}
