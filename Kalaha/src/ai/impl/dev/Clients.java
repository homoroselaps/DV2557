package ai.impl.dev;


import ai.impl.AIClientManager;
import ai.impl.DepthLevelSupplier;
import ai.impl.StartArrayDepthLevelSupplier;
import ai.impl.dev.MultiDebugging.PlayerMoveResult;
import kalaha.GameState;

import java.util.Random;




/**
 * Random and AI clients.
 * Created by Nejc on 1. 10. 2016.
 */
public final class Clients {


	private Clients() {
	}


	private static final Random random = new Random();




	private static void addText(String msg) {
		System.out.println(msg);
	}




	public static int getRandomMove(GameState gameState) {
		int selectedAmbo = 1 + (int) (random.nextDouble() * 6);
		while (!gameState.moveIsPossible(selectedAmbo))
			selectedAmbo = 1 + (int) (random.nextDouble() * 6);
		return selectedAmbo;
	}


	public static int getFirstMove(GameState gameState) {
		for (int i = 1; i <= 6; i++) {
			if (gameState.moveIsPossible(i))
				return i;
		}
		return -1;
	}


	public static int getAIMove(GameState gameState, boolean comment) {
		return getAIMoveAdvanced(gameState, comment).selectedAmbo;
	}


	public static PlayerMoveResult getAIMoveAdvanced(GameState currentBoard, boolean comment) {
		AIClientManager clientManager = AIClientManager.create(currentBoard, 99999999999L);
//		AIClientManager clientManager = AIClientManager.create(currentBoard, 4990L);
//		DepthLevelSupplier depthLevelSupplier = StartArrayDepthLevelSupplier.create(6, 6);
		DepthLevelSupplier depthLevelSupplier = StartArrayDepthLevelSupplier.create(10, 2, 6, 6);

		clientManager.run(depthLevelSupplier);

		if (comment) {
			addText("Depth reached: " + clientManager.getDepthReached());
			addText("Utility Value: " + clientManager.getRoot().getUtilityValue());
			addText("Selected ambo: " + clientManager.getRoot().getAmboToSelect());
		}

		int selectedMove = clientManager.getSelectedMove();
		PlayerMoveResult playerMoveResult = new PlayerMoveResult();
		playerMoveResult.selectedAmbo = selectedMove;
		playerMoveResult.depthReached = clientManager.getDepthReached();
		playerMoveResult.utilityValue = clientManager.getRoot().getUtilityValue();

		return playerMoveResult;
	}


	public static PlayerMoveResult getRandomMoveAdvanced(GameState gameState) {
		int res = getRandomMove(gameState);
		return new PlayerMoveResult(res);
	}


}
