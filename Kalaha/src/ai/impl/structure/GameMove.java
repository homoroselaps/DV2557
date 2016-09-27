package ai.impl.structure;


import kalaha.GameState;

import javax.management.remote.SubjectDelegationPermission;




/**
 * Represent a player's move.
 * Created by Nejc on 24. 09. 2016.
 */
public class GameMove implements Cloneable {


	private final GameState gameState;
	private final int selectedAmbo;
	private final boolean player;




	public GameState getGameState() {
		return gameState;
	}


	public int getSelectedAmbo() {
		return selectedAmbo;
	}


	/**
	 * Retrieves the player, who initiated the move.
	 *
	 * @return The player
	 */
	public boolean getPlayer() {
		return player;
	}


	public boolean getNextPlayer() {
		return !player;
	}



	private GameMove(GameState gameState, int selectedAmbo, boolean player) {
		this.gameState = gameState;
		this.selectedAmbo = selectedAmbo;
		this.player = player;
	}




	public static GameMove create(GameState gameState, boolean player) {
		return new GameMove(gameState, -1, player);
	}




	public GameMoveProvider getGameMoveProvider() {
		return new GameMoveProvider(this);
	}


	public GameMove makeMove(int selectedAmbo) {
		GameState newGameState = gameState.clone();
		newGameState.makeMove(selectedAmbo);

		boolean newPlayer = player;
		if (gameState.getNextPlayer() != newGameState.getNextPlayer())
			newPlayer = !newPlayer;

		return new GameMove(newGameState, selectedAmbo, newPlayer);
	}




	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new GameMove(gameState.clone(), selectedAmbo, player);
	}


	@Override
	public String toString() {
		return "Player: " + player + ", NextPlayer: " + getNextPlayer() + ", SelectedAmbo: " + selectedAmbo + ", GameState: " + gameState.toString();
	}


}
