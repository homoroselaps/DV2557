package ai.impl.tree;


import kalaha.GameState;

import java.util.ArrayList;
import java.util.Collections;

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
    public Node findBestChild(int maxDepth, int dontLookBelow, int dontLookAbove) {
        if(this.getDepth()<maxDepth && !this.getGameState().gameEnded()){
            // not a leaf node
            ArrayList<Node> values = new ArrayList<>();
            for(Node node : this){
                Node child = node.findBestChild(maxDepth, dontLookBelow, dontLookAbove);
                int val = child.getUtilityValue();
                if(val > dontLookBelow){
                    dontLookBelow = val;
                }
                if(val>dontLookAbove){
                    this.setUtilityValue(val);
                    return this;
                }
                values.add(child);
            }
            Node best = Collections.max(values);
            this.setUtilityValue(best.getUtilityValue());
            return this;
        } else {
            // leaf node
            final GameState game = this.getGameState();
            int maxScore = game.getScore(game.getNextPlayer());
            int minScore = game.getScore((game.getNextPlayer()%2)+1);
            this.setUtilityValue(maxScore - minScore);
            return this;
        }
    }
}
