package ai.impl.dev;


import ai.impl.AIClientManager;
import ai.impl.DepthLevelSupplier;
import ai.impl.StartArrayDepthLevelSupplier;
import ai.impl.structure.Tree;
import kalaha.GameState;




/**
 * Handles debugging.
 * Created by Nejc on 26. 09. 2016.
 */
public class Debugging {


	private GameState gameState;


	public Debugging() {
		gameState = new GameState();
	}




	private void run() {
		boolean startWithAI = true;
		while (!gameState.gameEnded()) {
			if (startWithAI) {
				System.out.print("AI's move ... ");
				int move = getAIMove(gameState);
				System.out.println(move);
				gameState.makeMove(move);
			} else {
				int move = getPlayerMove(gameState);
				System.out.println("Player's turn ... " + move);
				gameState.makeMove(move);
			}
			startWithAI = !startWithAI;
		}
		int winner = gameState.getWinner();
		System.out.println(winner >= 1
				? "Winner: " + (startWithAI ? "AI" : "Player")
				: "Draw"
		);
	}


	private void addText(String msg) {
		System.out.println(msg);
	}


	private int getPlayerMove(GameState currentBoard) {
		for (int i = 1; i <= 6; i++) {
			if (currentBoard.moveIsPossible(i))
				return i;
		}
		return -1;
	}


	private int getAIMove(GameState currentBoard) {
		Tree tree = Tree.create(currentBoard);
		DepthLevelSupplier depthLevelSupplier = StartArrayDepthLevelSupplier.create(3, 1, 2);
		AIClientManager aiClientManager = AIClientManager.fromTree(tree, 90000000L);

		aiClientManager.run(depthLevelSupplier);

		this.addText("Depth reached: " + aiClientManager.getDepthReached());
		return tree.getBestMove().getSelectedAmbo();
	}






	public static void main(String[] args) {
		//while (true) {
		System.out.println("Starting a new game...");
		Debugging d = new Debugging();
		d.run();
		//}
	}


}
