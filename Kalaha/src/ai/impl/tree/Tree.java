package ai.impl.tree;


/**
 * The tree structure of the MiniMax algorithm.
 * Created by Nejc on 23. 09. 2016.
 */
public class Tree {


	private Node root = new MaxNode(null);


	public Node getRoot() {
		return root;
	}


}
