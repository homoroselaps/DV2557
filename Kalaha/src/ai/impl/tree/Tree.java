package ai.impl.tree;


import kalaha.GameState;

/**
 * The tree structure of the MiniMax algorithm.
 * Created by Nejc on 23. 09. 2016.
 */
public class Tree {


    private Node root = new MaxNode(null);

    /**
     * Creates Tree with one {@link MaxNode} for the given game state
     * @param startingGame
     */
    public Tree(GameState startingGame) {
        this.root = new MaxNode(null, startingGame);
    }


    public Node getRoot() {
        return root;
    }


}
