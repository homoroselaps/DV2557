package ai.impl.structure;


import ai.impl.UtilityValueManager;

import java.util.ArrayList;
import java.util.List;

import static ai.impl.UtilityValueManager.NO_VALUE;




/**
 * Created by Nejc on 23. 09. 2016.
 */
public class Node {




	private final GameMove gameMove;
	private int levelsToAdd;
	private int amboToSelect = -1;
	private int utilityValue = NO_VALUE;




	/**
	 * Gets the move that resulted in this node.
	 *
	 * @return Game move that resulted in this node.
	 */
	public GameMove getGameMove() {
		return gameMove;
	}


	public int getLevelsToAdd() {
		return levelsToAdd;
	}


	public void setLevelsToAdd(int levelsToAdd) {
		this.levelsToAdd = levelsToAdd;
	}


	public int getAmboToSelect() {
		return amboToSelect;
	}


	public void setAmboToSelect(int amboToSelect) {
		this.amboToSelect = amboToSelect;
	}




	public int getUtilityValue() {
		return utilityValue;
	}


	public boolean hasUtilityValue() {
		return UtilityValueManager.hasValue(this.utilityValue);
	}


	public void setUtilityValue(int value) {
		this.utilityValue = value;
	}


	public void clearUtilityValue() {
		this.utilityValue = NO_VALUE;
	}




	/**
	 * Returns the next player.
	 *
	 * @return The player
	 */
	public boolean getNextPlayer() {
		return gameMove.getNextPlayer();
	}


	public boolean isLeaf() {
		return levelsToAdd <= 0 || gameMove.getGameState().gameEnded();
	}


	public boolean canHaveChildren() {
		return !isLeaf();
	}




	public Node(GameMove gameMove, int levelsToAdd) {
		this.gameMove = gameMove;
		this.levelsToAdd = levelsToAdd;
	}




	public Node createChild(GameMove gameMove) {
		Node node = new Node(gameMove, levelsToAdd - 1);
		return node;
	}




	@Override
	public String toString() {
		return "UtilityValue: " + utilityValue + /*", Children: " + children.size() +*/ ", GameMove: { " + gameMove.toString() + " }";
	}


}
