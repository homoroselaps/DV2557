package ai.impl;


import kalaha.GameState;

import java.util.Iterator;




/**
 * <p>Handles game move prediction.</p>
 * <p>Created by Nejc on 24. 09. 2016.</p>
 */
public class GameMoveProvider implements Iterable<GameMove> {


	private final GameMove gameMove;


	public GameMove getGameMove() {
		return gameMove;
	}




	/**
	 * Creates a new instance of GameMoveProvider.
	 *
	 * @param gameMove Associated game move
	 */
	public GameMoveProvider(GameMove gameMove) {
		this.gameMove = gameMove;
	}




	/**
	 * Gets an iterator which loops through all possible moves.
	 *
	 * @return The iterator
	 */
	@Override
	public Iterator<GameMove> iterator() {
		return new Itr(this);
	}








	/**
	 * An iterator that loops through all possible moves.
	 */
	protected static class Itr implements Iterator<GameMove> {


		/**
		 * Amount of ambos per player.
		 */
		private static final int AMBO_COUNT = 6;
		/**
		 * Minimum ambo index.
		 */
		private static final int MIN_AMBO_INDEX = 1;
		/**
		 * Maximum ambo index.
		 */
		private static final int MAX_AMBO_INDEX = MIN_AMBO_INDEX + AMBO_COUNT - 1;


		private int amboIndex;
		private final GameMove gameMove;


		/**
		 * Gets associated game move.
		 *
		 * @return Associated game move
		 */
		public GameMove getGameMove() {
			return gameMove;
		}




		/**
		 * Creates a new instance of Itr.
		 *
		 * @param gameMoveProvider Associated game move provider
		 */
		public Itr(GameMoveProvider gameMoveProvider) {
			this.gameMove = gameMoveProvider.getGameMove();
			this.amboIndex = MIN_AMBO_INDEX - 1;
			calculateNextAmboIndex();
		}




		/**
		 * Calculates the next available ambo index, if it exists.
		 */
		private void calculateNextAmboIndex() {
			GameState gameState = gameMove.getGameState();
			while (++amboIndex <= MAX_AMBO_INDEX) {
				if (gameState.moveIsPossible(amboIndex))
					break;
			}
		}


		/**
		 * Unused. Cannot remove an available move.
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException("Remove operation is not supported.");
		}


		/**
		 * Checks if any remaining move is available.
		 *
		 * @return Whether any move can be made
		 */
		@Override
		public boolean hasNext() {
			return amboIndex <= MAX_AMBO_INDEX;
		}


		/**
		 * Gets next available move.
		 *
		 * @return Next available game move
		 */
		@Override
		public GameMove next() {
			int index = amboIndex;
			calculateNextAmboIndex();
			return gameMove.makeMove(index);
		}


	}


}
