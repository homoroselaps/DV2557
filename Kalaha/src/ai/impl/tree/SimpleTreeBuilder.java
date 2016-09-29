package ai.impl.tree;

import kalaha.GameState;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * First implementation of {@link TreeBuilder}
 * Created by smarti on 24.09.16.
 */
public class SimpleTreeBuilder implements TreeBuilder{
    private final GameState startingGame;
    private final HashMap<Integer, LinkedList<Node>> nodeLevels;
    private final Tree tree;
    private int depth;

    /**
     * Creates new {@link SimpleTreeBuilder} based on a (starting) game state
     * @param game
     */
    public SimpleTreeBuilder(GameState game) {
        this.startingGame = game;
        nodeLevels = new HashMap<>();
        this.tree = new Tree(startingGame);
        addtoLevel(tree.getRoot(), 0);
    }

    /**
     * Adds node to internal datastructure for faster processing of nodes per depth-level
     * @param node
     * @param level
     */
    private void addtoLevel(Node node, int level){
        if(depth > level){
            this.depth = level;
        }
        List<Node> nodes = nodeLevels.getOrDefault(level, null);
        if(nodes != null){
            nodes.add(node);
        } else {
            nodeLevels.put(level, new LinkedList<>());
        }
    }

    @Override
    public Tree buildFurther(int levels) {
        for(int i=0; i<levels; i++) {
            buildNextLevel();
        }
        return tree;
    }

    /**
     * Extends the tree by one level
     */
    private void buildNextLevel(){
        nodeLevels.get(depth).stream().parallel()
                .forEach(node -> {
                    node.expandPossibleMoves();
                    node.getChildren().forEach(child -> addtoLevel(child, depth+1));
                });
    }

    @Override
    public Tree getCurrentTree() {
        return tree;
    }
}
