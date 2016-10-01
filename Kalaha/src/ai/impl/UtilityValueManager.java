package ai.impl;


import kalaha.GameState;




/**
 * <p>Handles utility value management.</p>
 * <p>Created by Nejc on 23. 09. 2016.</p>
 */
public final class UtilityValueManager {


	/**
	 * Prevent instantiation.
	 */
	private UtilityValueManager() {
	}




	/**
	 * A value that represents an undefined utility value.
	 */
	public static final int UNDEFINED = Integer.MIN_VALUE;




	/**
	 * Checks if given utility value is undefined.
	 *
	 * @param value Value to check
	 * @return Whether or not the value is undefined
	 */
	public static boolean isUndefined(int value) {
		return value != UNDEFINED;
	}


	/**
	 * Retrieves a utility value from game state.
	 *
	 * @param clientManager Associated {@link AIClientManager}
	 * @param gameMove      Game move from which the utility value should be extracted
	 * @return Utility value of given {@link GameMove}
	 */
	public static int getUtilityValueFromState(AIClientManager clientManager, GameMove gameMove) {
		GameState gameState = gameMove.getGameState();

		int valuePlayer = gameState.getScore(clientManager.getMaxPlayer());
		int valueOpponent = gameState.getScore(clientManager.getMinPlayer());

		return valuePlayer - valueOpponent;
	}


}
