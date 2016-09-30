package ai.impl.tree;


import kalaha.GameState;

/**
 * The tree structure of the MiniMax algorithm.
 * Created by Nejc on 23. 09. 2016.
 */
public class Tree {

    private Node root;

    /**
     * Creates Tree with one {@link MaxNode} for the given game state
     * @param startingGame
     */
    public Tree(GameState startingGame) {
        int player = startingGame.getNextPlayer();
        this.root = new MaxNode(player, startingGame);
    }


    public Node getRoot() {
        return root;
    }

}
