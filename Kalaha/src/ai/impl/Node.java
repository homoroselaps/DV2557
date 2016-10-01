package ai.impl;


import static ai.impl.UtilityValueManager.UNDEFINED;




/**
 * <p>Per-depth-first-exploration-iteration-level data holder that represents a node in a MiniMaxTree.</p>
 * <p>Created by Nejc on 23. 09. 2016.</p>
 */
public class Node {




	private final GameMove gameMove;
	private int levelsToAdd;
	private int amboToSelect = -1;
	private int utilityValue = UNDEFINED;




	/**
	 * Gets the game move that hols a {@link kalaha.GameState} object, which represent current node.
	 *
	 * @return The game move
	 */
	public GameMove getGameMove() {
		return gameMove;
	}


	/**
	 * Gets the amount of levels of the tree to be added after this node.
	 *
	 * @return The amount of levels to be added
	 */
	public int getLevelsToAdd() {
		return levelsToAdd;
	}


	/**
	 * Sets the amount of levels of the tree to be added after this node.
	 *
	 * @param levelsToAdd The amount of levels to be added
	 */
	public void setLevelsToAdd(int levelsToAdd) {
		this.levelsToAdd = levelsToAdd;
	}


	/**
	 * Gets the ambo that was selected in the previous turn.
	 *
	 * @return The previously selected ambo (1-6)
	 */
	public int getAmboToSelect() {
		return amboToSelect;
	}


	/**
	 * Gets the suggested ambo to select from current {@link kalaha.GameState}.
	 *
	 * @param amboToSelect Suggested ambo index to select (1-6)
	 */
	public void setAmboToSelect(int amboToSelect) {
		this.amboToSelect = amboToSelect;
	}




	/**
	 * Gets associated utility value.
	 *
	 * @return Associated utility value
	 * @see UtilityValueManager
	 */
	public int getUtilityValue() {
		return utilityValue;
	}


	/**
	 * Check if associated utility value is not undefined.
	 *
	 * @return Whether or not a utility value is present
	 * @see UtilityValueManager
	 * @see UtilityValueManager#UNDEFINED
	 * @see UtilityValueManager#isUndefined(int)
	 */
	public boolean hasUtilityValue() {
		return !UtilityValueManager.isUndefined(this.utilityValue);
	}


	/**
	 * Sets associated utility value.
	 *
	 * @param value Associated utility value
	 * @see UtilityValueManager
	 */
	public void setUtilityValue(int value) {
		this.utilityValue = value;
	}


	/**
	 * Clears associated utility value.
	 *
	 * @see UtilityValueManager
	 * @see UtilityValueManager#UNDEFINED
	 */
	public void clearUtilityValue() {
		this.utilityValue = UNDEFINED;
	}




	/**
	 * Gets the next player.
	 *
	 * @return The next player
	 * @see GameMove#getNextPlayer()
	 */
	public boolean getNextPlayer() {
		return gameMove.getNextPlayer();
	}


	/**
	 * Check if current node represents a leaf in the MiniMaxTree.
	 *
	 * @return Whether or not current node is a leaf
	 */
	public boolean isLeaf() {
		return levelsToAdd <= 0 || gameMove.getGameState().gameEnded();
	}


	/**
	 * Check whether the current node may have children (= is not a leaf).
	 *
	 * @return Whether or not the current node may have children
	 */
	public boolean canHaveChildren() {
		return !isLeaf();
	}




	/**
	 * Creates a new instance of {@link Node}.
	 *
	 * @param gameMove    Associated game move
	 * @param levelsToAdd Amount of levels of the tree to be added after this node
	 */
	public Node(GameMove gameMove, int levelsToAdd) {
		this.gameMove = gameMove;
		this.levelsToAdd = levelsToAdd;
	}




	/**
	 * Creates an underlying child {@link Node} (does not store the reference to the child, since we do not need it).
	 *
	 * @param gameMove Game move to perform to reach the child node
	 * @return A new child node
	 */
	public Node createChild(GameMove gameMove) {
		return new Node(gameMove, levelsToAdd - 1);
	}




	/**
	 * Gets a string representation of this component.
	 *
	 * @return String representation of this component
	 */
	@Override
	public String toString() {
		return "UtilityValue: " + utilityValue + /*", Children: " + children.size() +*/ ", GameMove: { " + gameMove.toString() + " }";
	}


}
