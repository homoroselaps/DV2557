package ai.impl;


import ai.impl.structure.Node;
import ai.impl.structure.Tree;
import ai.impl.util.Cancellable;

import java.util.ArrayList;
import java.util.Objects;




/**
 * Handles the logic behind creating and managing ai logic.
 * Created by Nejc on 24. 09. 2016.
 */
public class TreeBuilder implements Cancellable {


	private Tree tree;
	private ArrayList<Node> leaves;
	private TreeLevelBuilder treeLevelBuilder;
	private int depth;
	private boolean cancellationPending;
	private boolean running;


	public Tree getTree() {
		return tree;
	}


	public int getDepth() {
		return depth;
	}


	public boolean isCancellationPending() {
		return cancellationPending;
	}


	public void cancel() {
		if (treeLevelBuilder != null) {
			cancellationPending = true;
			treeLevelBuilder.cancel();
		}
	}


	public boolean isRunning() {
		return treeLevelBuilder != null;
	}




	public TreeBuilder(Tree tree) {
		Objects.requireNonNull(tree);
		this.tree = tree;
		this.leaves = new ArrayList<>(1);
		this.leaves.add(tree.getRoot());
	}




	public boolean build(int depth) {
		if (depth < 1)
			throw new IllegalArgumentException("Cannot build less than 1 level.");
		if (treeLevelBuilder != null)
			throw new IllegalStateException("Tree building already in progress.");

		treeLevelBuilder = new TreeLevelBuilder(tree, leaves, depth);
		treeLevelBuilder.run();
		boolean finished = treeLevelBuilder.isFinished();

		if (finished) {
			// treeLevelBuilder has successfully finished its work
			leaves = treeLevelBuilder.getNewLeaves();
			this.depth += depth;
		}

		treeLevelBuilder = null; // let GC do it's work
		this.cancellationPending = false;

		return finished;


	}


}
