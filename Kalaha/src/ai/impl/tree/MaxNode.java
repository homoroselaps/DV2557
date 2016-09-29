package ai.impl.tree;


import kalaha.GameState;

/**
 * The Maximizer node implementation of {@link Node}.
 * Created by Nejc on 23. 09. 2016.
 */
public class MaxNode extends Node {


    public MaxNode(Node parent) {
        super(parent);
    }

    public MaxNode(Node parent, GameState game) {
        super(parent, game);
    }

    @Override
    public void expandPossibleMoves() {
        GameState game = this.getGameState();
        int player = game.getNextPlayer();
        for(int i = 1; i<7; i++){
            if(game.moveIsPossible(i)){
                GameState childGame = game.clone();
                childGame.makeMove(i);
                Node child;
                if(childGame.getNextPlayer()==player){
                    child = new MaxNode(this, childGame);
                } else {
                    child = new MinNode(this, childGame);
                }
                this.getChildren().add(child);
            }
        }
    }
}
