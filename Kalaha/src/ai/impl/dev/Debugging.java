package ai.impl.dev;


import ai.impl.AIClientManager;
import ai.impl.DepthLevelSupplier;
import ai.impl.StartArrayDepthLevelSupplier;
import kalaha.GameState;

import javax.swing.text.AsyncBoxView;
import java.util.Random;




/**
 * Handles debugging.
 * Created by Nejc on 26. 09. 2016.
 */
public class Debugging {


	private GameState gameState;
	private Random random = new Random();




	public Debugging() {
		gameState = new GameState();
	}




	public static void main(String[] args) {
		//while (true) {
		System.out.println("Starting a new game...\n");
		Debugging d = new Debugging();
		d.run();
		//}
	}


	private static int getRandom() {
		return 1 + (int) (Math.random() * 6);
	}


	protected void addText(String msg) {
		System.out.println(msg);
	}


	private int getOpponentMove(GameState currentBoard) {
		int selectedAmbo = getRandom();
		while (!currentBoard.moveIsPossible(selectedAmbo))
			selectedAmbo = getRandom();
		return selectedAmbo;
	}




	private void run() {
		boolean startWithAI = true;
		int ai = startWithAI ? 1 : 2;
		int opponent = (ai % 2) + 1;

		while (!gameState.gameEnded()) {
			if (gameState.getNextPlayer() == ai) {

				System.out.println("AI's move ... ");

				TimeTracker.PUBLIC_INSTANCE.start();
				int move = getAIMove(gameState);
				TimeTracker.PUBLIC_INSTANCE.stopPrint();

				gameState.makeMove(move);

			} else {

				int move = getOpponentMove(gameState);
				System.out.println("Opponent's turn ... "); // + move);
				gameState.makeMove(move);
			}

			this.addText("");
		}

		int winner = gameState.getWinner();
		if (winner >= 1) {
			System.out.println("Winner: " + (winner == ai ? "AI" : "Player"));
			System.out.println("---------------------");
			System.out.println("Score AI: " + gameState.getScore(ai));
			System.out.println("Score Opp: " + gameState.getScore(opponent));
			System.out.println("Score Diff: " + (gameState.getScore(winner) - gameState.getScore((winner % 2) + 1)));
		} else {
			System.out.println("Draw");
		}

	}




	private int getAIMove(GameState currentBoard) {
		AIClientManager clientManager = AIClientManager.create(currentBoard, 99999999999L);
//		AIClientManager clientManager = AIClientManager.create(currentBoard, 4990L);

		DepthLevelSupplier depthLevelSupplier = StartArrayDepthLevelSupplier.create(6, 6);
//		DepthLevelSupplier depthLevelSupplier = StartArrayDepthLevelSupplier.createNoLimit(1, 2);

		clientManager.run(depthLevelSupplier);

		addText("Depth reached: " + clientManager.getDepthReached());
		addText("Utility Value: " + clientManager.getRoot().getUtilityValue());
		addText("Selected ambo: " + clientManager.getRoot().getAmboToSelect());

		return clientManager.getSelectedMove();
	}


}
