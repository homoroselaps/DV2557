package ai.impl.structure;


import ai.impl.PruningManager;
import ai.impl.UtilityValueManager;
import kalaha.GameState;




/**
 * The structure structure of the MiniMax algorithm.
 * Created by Nejc on 23. 09. 2016.
 */
public class Tree {


	private final PruningManager pruningManager;
	private final UtilityValueManager utilityValueManager;
	private final Node root;
	private final int maxPlayer;
	private final int minPlayer;




	public Node getRoot() {
		return root;
	}


	public PruningManager getPruningManager() {
		return pruningManager;
	}


	public UtilityValueManager getUtilityValueManager() {
		return utilityValueManager;
	}




	private Tree(Node root) {
		this.root = root;

		GameMove gameMove = root.getGameMove();
		GameState gameState = gameMove.getGameState();
		int player = gameState.getNextPlayer();

		if (root.getNextPlayer()) {
			// this is maximizer
			this.maxPlayer = player;
			this.minPlayer = (player + 1) % 2;
		} else {
			// this is maximizer
			this.maxPlayer = (player + 1) % 2;
			this.minPlayer = player;
		}

		this.pruningManager = new PruningManager();
		this.utilityValueManager = new UtilityValueManager(this);
	}




	public static Tree create(GameState gameState) {
		GameMove gameMove = GameMove.create(gameState, true);
		Node node = new Node(null, gameMove);
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
		return root
				.getChildren()
				.stream()
				.max((a, b) -> Integer.compare(b.getUtilityValue(), a.getUtilityValue()))
				.filter(node -> node.hasUtilityValue())
				.orElseThrow(() -> new IllegalStateException("No valid node"));

	}


	public GameMove getBestMove() {
		return getBestMoveNode().getGameMove();
	}


}
