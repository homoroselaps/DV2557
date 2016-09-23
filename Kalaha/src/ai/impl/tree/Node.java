package ai.impl.tree;


import ai.impl.UtilityManager;
import com.sun.istack.internal.Nullable;
import kalaha.GameState;

import java.util.ArrayList;




/**
 * A node of the {@link Tree}
 * Created by Nejc on 23. 09. 2016.
 */
public abstract class Node {




	private static final int CHILDREN_LIST_INITIALIZATION = 6;




	private final Node parent;
	private final ArrayList<Node> children = new ArrayList<>(CHILDREN_LIST_INITIALIZATION);
	private GameState gameState;
	private int utilityValue;




	public
	@Nullable
	Node getParent() {
		return parent;
	}


	public ArrayList<Node> getChildren() {
		return children;
	}


	public int getUtilityValue() {
		return utilityValue;
	}


	public boolean hasUtilitlyValue() {
		return UtilityManager.hasValue(this.utilityValue);
	}


	public void setUtilityValue(int value) {
		this.utilityValue = value;
	}


	public
	@Nullable
	GameState getGameState() {
		return gameState;
	}


	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}




	public Node(@Nullable Node parent) {
		this.parent = parent;
	}


}