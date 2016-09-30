package ai.impl.tree;


import kalaha.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * The Maximizer node implementation of {@link Node}.
 * Created by Nejc on 23. 09. 2016.
 */
public class MaxNode extends Node {


    public MaxNode(int currentPlayer, GameState game) {
        super(currentPlayer, game);
    }

    public MaxNode(Node parent, GameState game, int lastMove) {
        super(parent, game, lastMove);
    }

    @Override
    public Node getChildForMove(int i) {
        Node result = null;
        GameState next = getGameState().clone();
        next.makeMove(i);
        if(next.getNextPlayer() == getCurrentPlayer()){
            result = new MaxNode(this, next, i);
        } else {
            result = new MinNode(this, next, i);
        }
        return result;
    }

    @Override
    public int getUtilityValue(int maxDepth, int dontLookBelow, int dontLookAbove) {
        if(this.getDepth()<=maxDepth && !this.getGameState().gameEnded()){
            // not a leaf node
            ArrayList<Integer> values = new ArrayList<>();
            for(Node node : this){
                int val = node.getUtilityValue(maxDepth, dontLookBelow, dontLookAbove);
                if(val > dontLookBelow){
                    dontLookBelow = val;
                }
                if(val>dontLookAbove){
                    return val;
                }
            }
            return Collections.max(values);
        } else {
            // leaf node
            final GameState game = this.getGameState();
            return game.getScore(game.getNextPlayer());
        }
    }

}
