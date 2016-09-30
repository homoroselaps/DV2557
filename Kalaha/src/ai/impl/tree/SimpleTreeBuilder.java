package ai.impl.tree;

import kalaha.GameState;

import java.util.*;

/**
 * First implementation of {@link TreeBuilder}
 * Created by smarti on 24.09.16.
 */
public class SimpleTreeBuilder implements TreeBuilder{
    private final GameState startingGame;
    private final Tree tree;
    private int depth;

    /**
     * Creates new {@link SimpleTreeBuilder} based on a (starting) game state
     * @param game
     */
    public SimpleTreeBuilder(GameState game) {
        this.startingGame = game;
        this.tree = new Tree(startingGame);
    }

    @Override
    public void buildUntil(int level) {
        tree.getRoot().getUtilityValue();
    }

    public int getBestMove(){
        Collection<Node> alternatives = tree.getRoot().getChildren().values();
        Node best = Collections.max(alternatives, new Comparator<Node>() {
            @Override
            public int compare(Node node, Node t1) {
                return node.g;
            }
        })
    }
}
