package ai.impl.dev;


import kalaha.GameState;

import java.util.Random;




/**
 * Handles debugging.
 * Created by Nejc on 26. 09. 2016.
 */
public class Debugging {


	private final GameState gameState;
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


	protected void addText(String msg) {
		System.out.println(msg);
	}




	private void run() {
		boolean startWithAI = true;
		int ai = startWithAI ? 1 : 2;
		int opponent = (ai % 2) + 1;

		while (!gameState.gameEnded()) {
			if (gameState.getNextPlayer() == ai) {

				System.out.println("AI's move ... ");

				TimeTracker.PUBLIC_INSTANCE.start();
				int move = Clients.getAIMove(gameState, true);
				TimeTracker.PUBLIC_INSTANCE.stopPrint();

				gameState.makeMove(move);

			} else {

				int move = Clients.getRandomMove(gameState);
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


}
