package ai.impl;


import kalaha.GameState;




/**
 * <p>Contains general game-related functionalities.</p>
 * <p>Created by Nejc on 24. 09. 2016.</p>
 */
public final class GameUtility {


	/**
	 * Prevent instantiation.
	 */
	private GameUtility() {
	}




	/**
	 * Gets the opponent of given player.
	 *
	 * @param player {@link Boolean} representation of given player
	 * @return The opponent of given player
	 */
	public static boolean getOpponent(boolean player) {
		return !player;
	}


	/**
	 * Gets the opponent of given player.
	 *
	 * @param player {@link Integer} representation of player (1 or 2)
	 * @return The opponent of given player
	 */
	public static int getOpponent(int player) {
		return player == 1
				? 2
				: 1;
	}




	/**
	 * Gets the first available move
	 *
	 * @param gameState {@link GameState} from which to determine the first possible move
	 * @return The first possible move (the lowest possible integer from 1-6), or -1 if no moves are available
	 */
	public static int getFirstAvailableMove(GameState gameState) {
		for (int i = 1; i <= 6; i++) {
			if (gameState.moveIsPossible(i))
				return i;
		}
		return -1;
	}


}
