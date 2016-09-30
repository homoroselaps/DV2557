package ai.impl.structure;


import ai.impl.UtilityValueManager;

import java.util.ArrayList;

import static ai.impl.UtilityValueManager.NO_VALUE;




/**
 * A node of the {@link Tree}
 * Created by Nejc on 23. 09. 2016.
 */
public class Node {




	private static final int CHILDREN_LIST_INITIALIZATION = 6;




	private final Node parent;
	private final ArrayList<Node> children = new ArrayList<>(CHILDREN_LIST_INITIALIZATION);
	private final GameMove gameMove;
	private int utilityValue = NO_VALUE;
	private int levelsToAdd;
	private int pruningValue;




	public Node getParent() {
		return parent;
	}


	public boolean hasParent() {
		return parent != null;
	}


	public ArrayList<Node> getChildren() {
		return children;
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
	 * Gets the move that resulted in this node.
	 *
	 * @return Game move that resulted in this node.
	 */
	public GameMove getGameMove() {
		return gameMove;
	}


	/**
	 * Returns the next player.
	 *
	 * @return The player
	 */
	public boolean getNextPlayer() {
		return gameMove.getNextPlayer();
	}


	public int getLevelsToAdd() {
		return levelsToAdd;
	}


	public void setLevelsToAdd(int levelsToAdd) {
		this.levelsToAdd = levelsToAdd;
	}


	public int getPruningValue() {
		return pruningValue;
	}


	public void setPruningValue(int value) {
		this.pruningValue = value;
	}


	public boolean isLeaf() {
		return levelsToAdd <= 0 || gameMove.getGameState().gameEnded();
	}


	public boolean canHaveChildren() {
		return !isLeaf();
	}




	public Node(Node parent, GameMove gameMove, int levelsToAdd) {
		this.parent = parent;
		this.gameMove = gameMove;
		this.levelsToAdd = levelsToAdd;
	}




	public Node createChild(GameMove gameMove) {
		Node node = new Node(this, gameMove, levelsToAdd - 1);
		this.children.add(node);
		return node;
	}


	public int countSubNodes(boolean includeSelf) {
		int count = includeSelf ? 1 : 0;
		for (Node child : children) {
			count += child.countSubNodes(true);
		}
		return count;
	}


	@Override
	public String toString() {
		return "UtilityValue: " + utilityValue + ", Children: " + children.size() + ", GameMove: { " + gameMove.toString() + " }";
	}


}
