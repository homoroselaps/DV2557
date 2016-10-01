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
    private int lastDepth;
    private int lastUtil;

    /**
     * Creates new {@link SimpleTreeBuilder} based on a (starting) game state
     * @param game
     */
    public SimpleTreeBuilder(GameState game) {
        this.startingGame = game;
        this.tree = new Tree(startingGame);
    }

    @Override
    public int getBestMove(int level) {
        Node best = tree.getRoot().findBestChild(level, Integer.MIN_VALUE, Integer.MAX_VALUE);
        lastUtil = best.getUtilityValue();
        lastDepth = level;
        int move = Collections.max(best.getChildren().values()).getLastMove();
        return move;
    }

    public int getLastDepth() {
        return lastDepth;
    }

    public int getLastUtil() {
        return lastUtil;
    }
}
