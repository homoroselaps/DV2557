package ai.impl;


import kalaha.GameState;




/**
 * Created by Nejc on 24. 09. 2016.
 */
public final class GameUtility {


	private GameUtility() {
	}




	public static boolean getOpponent(boolean player) {
		return !player;
	}


	public static int getOpponent(int player) {
		return player == 1
				? 2
				: 1;
	}




	public static int getFirstAvailableMove(GameState gameState) {
		for (int i = 1; i <= 6; i++) {
			if (gameState.moveIsPossible(i))
				return i;
		}
		return -1;
	}


}
