package ai.impl.structure;


import kalaha.GameState;

import java.util.Iterator;




/**
 * Handles the logic of connecting the AI logic with the game itself.
 * Created by Nejc on 24. 09. 2016.
 */
public class GameMoveProvider implements Iterable<GameMove> {


	private final GameMove gameMove;


	public GameMove getGameMove() {
		return gameMove;
	}




	public GameMoveProvider(GameMove gameMove) {
		this.gameMove = gameMove;
	}




	@Override
	public Iterator<GameMove> iterator() {
		return new Itr(this);
	}








	protected static class Itr implements Iterator<GameMove> {


		private static final int AMBO_COUNT = 6; // TODO: debug
		private static final int MIN_AMBO_INDEX = 1;
		private static final int MAX_AMBO_INDEX = MIN_AMBO_INDEX + AMBO_COUNT - 1;


		private int amboIndex;
		private final GameMove gameMove;


		public GameMove getGameMove() {
			return gameMove;
		}




		public Itr(GameMoveProvider gameMoveProvider) {
			this.gameMove = gameMoveProvider.getGameMove();
			this.amboIndex = MIN_AMBO_INDEX - 1;
			calculateNextAmboIndex();
		}




		private void calculateNextAmboIndex() {
			GameState gameState = gameMove.getGameState();
			while (++amboIndex <= MAX_AMBO_INDEX) {
				if (gameState.moveIsPossible(amboIndex))
					break;
			}
		}


		@Override
		public void remove() {
			throw new UnsupportedOperationException("Remove operation is not supported.");
		}


		@Override
		public boolean hasNext() {
			return amboIndex <= MAX_AMBO_INDEX;
		}


		@Override
		public GameMove next() {
			int index = amboIndex;
			calculateNextAmboIndex();
			return gameMove.makeMove(index);
		}


	}


}
