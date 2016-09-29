package ai.impl.tree;


import kalaha.GameState;

/**
 * The Minimizer implementation of {@link Node}.
 * Created by Nejc on 23. 09. 2016.
 */
public class MinNode extends Node {


    public MinNode(Node parent) {
        super(parent);
    }

    public MinNode(Node parent, GameState game) {
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
                    child = new MinNode(this, childGame);
                } else {
                    child = new MaxNode(this, childGame);
                }
                this.getChildren().add(child);
            }
        }
    }
}
