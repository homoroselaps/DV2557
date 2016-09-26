package ai.impl;


import ai.impl.structure.GameMove;
import ai.impl.structure.GameMoveProvider;
import ai.impl.structure.Node;
import ai.impl.structure.Tree;
import ai.impl.util.Cancellable;

import java.util.*;




/**
 * Handles logic for building the a structure.
 * Created by Nejc on 23. 09. 2016.
 */
public class TreeLevelBuilder implements Runnable, Cancellable {




	private final Tree tree;
	private final ArrayList<Node> leaves;
	private final ArrayList<Node> newLeaves;
	private final int depth;
	private int[] leafUtilityValues;
	private volatile boolean cancellationPending;
	private boolean finished;
	private boolean running;




	public Tree getTree() {
		return tree;
	}


	public ArrayList<Node> getLeaves() {
		return leaves;
	}


	public ArrayList<Node> getNewLeaves() {
		return newLeaves;
	}


	public int getDepth() {
		return depth;
	}


	public boolean isCancellationPending() {
		return cancellationPending;
	}


	public void cancel() {
		this.cancellationPending = true;
	}


	public boolean isFinished() {
		return finished;
	}


	public boolean isRunning() {
		return running;
	}




	public TreeLevelBuilder(Tree tree, ArrayList<Node> leaves, int depth) {
		this.tree = tree;
		this.leaves = leaves;
		this.newLeaves = new ArrayList<>();
		this.depth = depth;
	}




	public void run() {
		start();
		cloneLeafUtilityValues();
		if (checkCancellationPending()) return;
		buildTree();
		if (checkCancellationPending()) return;
		updateTreeUtilityValues();
		end(true);
	}


	private void start() {
		if (finished)
			throw new IllegalStateException("Already finished.");
		if (running)
			throw new IllegalStateException("Already running.");

		running = true;
	}


	private void end(boolean finished) {
		this.running = false;
		this.finished = finished;
	}


	private boolean checkCancellationPending() {
		if (!cancellationPending || !running)
			return false;

		end(false);
		return true;
	}




	private void buildTree() {
		for (Node node : leaves) {
			State state = State.createRoot(node, depth);
		}
	}


	/**
	 * The recursive-calling method used to build the structure.
	 *
	 * @param state The state of the iteration
	 */
	private void step(State state) {
		GameMoveProvider gameMoveProvider = state.getNode().getGameMove().getGameMoveProvider();

		if (cancellationPending)
			return; // exit

		if (state.canHaveChildren()) {

			List<Node> children = state.getNode().getChildren();
			if (children.size() != 0)
				children.clear(); // we'll overwrite any existing nodes

			boolean pruned = false;

			for (GameMove gameMove : gameMoveProvider) {

				// create children
				State child = state.createChild(gameMove);
				step(child);

				// this node may have requested pruning
				if (state.shouldPrune()) {
					pruned = true;
					break;
				}

			}

			// if we haven't pruned, then the utility value for current node must be updated
			if (!pruned)
				tree.getUtilityValueManager().assignFromChildren(state.getNode());

		} else {
			// handle leaf node logic (parent of this node *does* exist)
			Node node = state.getNode();

			newLeaves.add(node);

			tree.getUtilityValueManager().assignFromGameState(node);

			boolean prune = tree.getPruningManager().resolveNodeValue(node);
			if (prune) {
				state.requestPruning();
				state.updateParentUtilityValue(); // propagate utility value to the parent
			}

		}
	}


	private void cloneLeafUtilityValues() {
		leafUtilityValues = new int[leaves.size()];

		for (int i = 0; i < leaves.size(); i++) {
			leafUtilityValues[i] = leaves.get(i).getUtilityValue();
		}
	}


	private void updateTreeUtilityValues() {
		// some node's utilityValues may have changed
		// therefore, we need to propagate these changes towards the root of the structure

		ArrayList<Node> nodesThatNeedUpdate = new ArrayList<>(leaves.size());

		for (int i = 0; i < leaves.size(); i++) {
			int value = leafUtilityValues[i];
			Node node = leaves.get(i);
			int newValue = node.getUtilityValue();
			if (value != newValue)
				nodesThatNeedUpdate.add(node);
		}


		// sort by descending utility value
		//nodesThatNeedUpdate.sort((o1, o2) -> o1.getUtilityValue() - o2.getUtilityValue());
		Collections.sort(nodesThatNeedUpdate, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				return o1.getUtilityValue() - o2.getUtilityValue();
			}
		});

		// do the update
		//nodesThatNeedUpdate.forEach(node -> UtilityValueManager.updateNodeAncestorUtilityValues(node));
		for (Node node : nodesThatNeedUpdate) {
			UtilityValueManager.updateNodeAncestorUtilityValues(node);
		}

	}








	private static class State {


		private final Node node;
		private final int levelsToAdd;
		private final State parent;
		private boolean prune;




		public Node getNode() {
			return node;
		}


		public int getLevelsToAdd() {
			return levelsToAdd;
		}


		public State getParent() {
			return parent;
		}


		public boolean shouldPrune() {
			return prune;
		}


		public void setPrune(boolean prune) {
			this.prune = prune;
		}


		public boolean hasParent() {
			return parent != null;
		}


		public boolean canHaveChildren() {
			return levelsToAdd > 0; // if (levelsToAdd <= 0) { abortion(); } // no children
		}


		public boolean isLeafNode() {
			return levelsToAdd <= 0;
		}




		private State(final Node node, final State parent, final int levelsToAdd) {
			this.node = node;
			this.parent = parent;
			this.levelsToAdd = levelsToAdd;
		}




		public static State createRoot(Node rootNode, int levelsToAdd) {
			return new State(rootNode, null, levelsToAdd);
		}




		public State createChild(Node childNode) {
			return new State(childNode, this, levelsToAdd - 1);
		}


		public State createChild(GameMove gameMove) {
			Node childNode = node.createChild(gameMove);
			return createChild(childNode);
		}


		public void requestPruning() {
			this.prune = true;
			if (parent != null) {
				parent.prune = true;
			}
		}


		public void updateParentUtilityValue() {
			this.parent.node.setUtilityValue(this.node.getUtilityValue());
		}


	}


}
