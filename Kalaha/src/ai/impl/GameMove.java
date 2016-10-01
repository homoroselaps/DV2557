package ai.impl;


import kalaha.GameState;




/**
 * <p>Represent player's move.</p>
 * <p>Created by Nejc on 24. 09. 2016.</p>
 */
public class GameMove implements Cloneable {




	private final GameState gameState;
	private final int selectedAmbo;
	/**
	 * The next player.
	 */
	private final boolean player;




	/**
	 * Gets the associated game state.
	 *
	 * @return Associated game state
	 */
	public GameState getGameState() {
		return gameState;
	}


	/**
	 * Gets the selected ambo in previous turn that lead to current {@link GameMove#gameState}.
	 *
	 * @return Previously selected ambo index (1-6)
	 */
	public int getSelectedAmbo() {
		return selectedAmbo;
	}


	/**
	 * Gets the player who is on turn.
	 *
	 * @return The next player
	 */
	public boolean getNextPlayer() {
		return player;
	}




	/**
	 * Creates a new instance of GameMove.
	 *
	 * @param gameState    Associated game state
	 * @param selectedAmbo Previously selected ambo
	 * @param player       Next player
	 */
	private GameMove(GameState gameState, int selectedAmbo, boolean player) {
		this.gameState = gameState;
		this.selectedAmbo = selectedAmbo;
		this.player = player;
	}




	/**
	 * Creates a new GameMove.
	 *
	 * @param gameState Associated game state
	 * @param player    Next player
	 * @return A new instance of GameMove
	 */
	public static GameMove create(GameState gameState, boolean player) {
		return new GameMove(gameState, -1, player);
	}




	/**
	 * Gets associated game move provider.
	 *
	 * @return Associated game move provider
	 */
	public GameMoveProvider getGameMoveProvider() {
		return new GameMoveProvider(this);
	}


	/**
	 * Gets a game move which represents a game state after making a move.
	 *
	 * @param selectedAmbo Next player's move (selected ambo index; 1-6)
	 * @return A new instance of GameMove
	 */
	public GameMove makeMove(int selectedAmbo) {
		GameState newGameState = gameState.clone();
		newGameState.makeMove(selectedAmbo);

		boolean newPlayer = player;
		if (gameState.getNextPlayer() != newGameState.getNextPlayer())
			newPlayer = !newPlayer;

		return new GameMove(newGameState, selectedAmbo, newPlayer);
	}




	/**
	 * Clones current game move.
	 *
	 * @return A clone of current game move, with cloned GameState
	 * @throws CloneNotSupportedException Cloning may be unsupported in any inherited classes
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new GameMove(gameState.clone(), selectedAmbo, player);
	}


	/**
	 * Gets a string representation of current game move.
	 *
	 * @return String representation of current move
	 */
	@Override
	public String toString() {
		return "NextPlayer: " + getNextPlayer() + ", GameState: " + gameState.toString();
	}


}
