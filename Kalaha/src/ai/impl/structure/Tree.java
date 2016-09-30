package ai.impl.structure;


import ai.impl.PruningManager;
import ai.impl.UtilityValueManager;
import kalaha.GameState;

import java.util.List;




/**
 * The structure structure of the MiniMax algorithm.
 * Created by Nejc on 23. 09. 2016.
 */
public class Tree {


	private final Node root;
	private final int maxPlayer;
	private final int minPlayer;




	public Node getRoot() {
		return root;
	}


	public int getMaxPlayer() {
		return maxPlayer;
	}


	public int getMinPlayer() {
		return minPlayer;
	}




	private Tree(Node root) {
		this.root = root;

		GameMove gameMove = root.getGameMove();
		GameState gameState = gameMove.getGameState();
		int player = gameState.getNextPlayer();

		if (root.getNextPlayer()) {
			// this is maximizer
			this.maxPlayer = player;
			this.minPlayer = (player % 2) + 1;
		} else {
			// this is maximizer
			this.maxPlayer = (player % 2) + 1;
			this.minPlayer = player;
		}
	}




	public static Tree create(GameState gameState) {
		GameMove gameMove = GameMove.create(gameState, true);
		Node node = new Node(null, gameMove, 0);
		return new Tree(node);
	}




	public int getPlayer(boolean player) {
		return player
				? maxPlayer
				: minPlayer;
	}


	public boolean getPlayer(int player) {
		return player == maxPlayer;
	}


	public int getOpponent(boolean player) {
		return player
				? minPlayer
				: maxPlayer;
	}


	public boolean getOpponent(int player) {
		return player == minPlayer;
	}


	public Node getBestMoveNode() {
//		return root
//				.getChildren()
//				.stream()
//				.max((a, b) -> Integer.compare(b.getUtilityValueFromState(), a.getUtilityValueFromState()))
//				.filter(node -> node.hasUtilityValue())
//				.orElseThrow(() -> new IllegalStateException("No valid node."));

		List<Node> children = root.getChildren();
		if (children.size() == 0)
			throw new IllegalStateException("No nodes available.");

		Node best = children.get(0);
		for (int i = 1; i < children.size(); i++) {
			Node node = children.get(i);
			if (node.getUtilityValue() > best.getUtilityValue())
				best = node;
		}

		if (!best.hasUtilityValue())
			throw new IllegalStateException("No valid node.");

		return best;
	}


	public GameMove getBestMove() {
		return getBestMoveNode().getGameMove();
	}


	public int countNodes() {
		return root.countSubNodes(true);
	}


	@Override
	public String toString() {
		return "MaxPlayer: " + maxPlayer + ", MinPlayer: " + minPlayer + ", Root: { " + root.toString() + " }";
	}


}
