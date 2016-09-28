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
	private final TreeBuilder treeBuilder;
	private boolean cancellationPending;
	private boolean running;
	private int depthReached;




	public TreeBuilder getTreeBuilder() {
		return treeBuilder;
	}


	public boolean isCancellationPending() {
		return cancellationPending;
	}


	public void cancel() {
		this.cancellationPending = true;
	}


	public boolean isRunning() {
		return running;
	}


	public int getDepthReached() {
		return depthReached;
	}




	public AIClientManager(TreeBuilder treeBuilder, long timeout) {
		this.treeBuilder = treeBuilder;
		this.cancellationTimer = new CancellationTimer(treeBuilder, timeout);
	}


	public AIClientManager(TreeBuilder treeBuilder) {
		this(treeBuilder, DEFAULT_TIMEOUT);
	}




	public static AIClientManager fromTree(Tree tree) {
		return new AIClientManager(new TreeBuilder(tree));
	}


	public static AIClientManager fromTree(Tree tree, long timeout) {
		return new AIClientManager(new TreeBuilder(tree), timeout);
	}




	private void start() {
		if (running)
			throw new IllegalStateException("Already running.");

		cancellationTimer.start();
		this.cancellationPending = false;
		running = true;
	}


	private void end() {
		cancellationTimer.cancel();
		running = false;
		cancellationPending = false;
	}


	public boolean run(int customDepthLevel) {
		start();

		treeBuilder.build(customDepthLevel);
		if (!treeBuilder.isCancellationPending())
			depthReached += customDepthLevel;

		end();
		return treeBuilder.isCancellationPending();
	}


	public boolean run(DepthLevelSupplier depthLevelSupplier) {
		start();
		for (int depth : depthLevelSupplier) {
			treeBuilder.build(depth);
			if (this.cancellationPending || treeBuilder.isCancellationPending()) {
				end();
				return false;
			} else {
				this.depthReached += depth;
			}
		}
		end();
		return true;
	}


}
