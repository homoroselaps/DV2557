package ai.impl.tree;


import kalaha.GameState;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * A node of the {@link Tree}
 * Created by Nejc on 23. 09. 2016.
 */
public abstract class Node implements Iterable<Node>, Comparable<Node>{


    private static final int CHILDREN_LIST_INITIALIZATION = 6;

    private final Node parent;
    private final Map<Integer, Node> children = new HashMap<>(CHILDREN_LIST_INITIALIZATION);
    private GameState gameState;
    private int lastMove;
    private int currentPlayer;
    private int utilityValue;
    private int depth;

    public Node getParent() {
        return parent;
    }

    public int getUtilityValue() {
        return utilityValue;
    }

    public Map<Integer, Node> getChildren(){
        return children;
    }

    public void setUtilityValue(int value) {
        this.utilityValue = value;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public int getLastMove() {
        return lastMove;
    }

    public void setLastMove(int lastMove) {
        this.lastMove = lastMove;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Node(int currentPlayer, GameState game) {
        this.parent = null;
        this.gameState = game;
        this.currentPlayer = currentPlayer;
        this.depth = 0;
        this.lastMove = -1;
    }

    public Node(Node parent, GameState game, int lastMove) {
        this.parent = parent;
        this.depth = parent.getDepth() + 1;
        this.currentPlayer = game.getNextPlayer();
        this.gameState = game;
        this.lastMove = lastMove;
    }

    public abstract Node getChildForMove(int i);

    public abstract Node findBestChild(int maxDepth, int dontLookBelow, int dontLookAbove);

    @Override
    public Iterator<Node> iterator() {
        return new Iterator<Node>() {
            int ambo = 0;
            @Override
            public boolean hasNext() {
                for(int i=ambo+1; i<7; i++){
                    if(getGameState().moveIsPossible(i)){
                        ambo = i;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Node next() {
                Node result = getChildren().get(ambo);
                if(result==null){
                    Node newNode = getChildForMove(ambo);
                    getChildren().put(ambo, newNode);
                    result = newNode;
                }
                return result;
            }
        };
    }

    @Override
    public int compareTo(Node node) {
        return getUtilityValue() - node.getUtilityValue();
    }
}
