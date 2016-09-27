package ai.impl.structure;


import ai.impl.UtilityValueManager;

import java.util.ArrayList;




/**
 * A node of the {@link Tree}
 * Created by Nejc on 23. 09. 2016.
 */
public class Node {




	private static final int CHILDREN_LIST_INITIALIZATION = 6;




	private final Node parent;
	private final ArrayList<Node> children = new ArrayList<>(CHILDREN_LIST_INITIALIZATION);
	private final GameMove gameMove;
	private int utilityValue;




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
		this.utilityValue = UtilityValueManager.NO_VALUE;
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




	public Node(Node parent, GameMove gameMove) {
		this.parent = parent;
		this.gameMove = gameMove;
	}




	public Node createChild(GameMove gameMove) {
		Node node = new Node(this, gameMove);
		this.children.add(node);
		return node;
	}


	@Override
	public String toString() {
		return "UtilityValue: " + utilityValue + ", Children: " + children.size() + ", GameMove: " + gameMove.toString();
	}


}
