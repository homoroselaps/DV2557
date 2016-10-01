package ai.impl.tree.dev;


import ai.impl.tree.SimpleTreeBuilder;
import ai.impl.tree.TimedBuilder;
import ai.impl.tree.TreeBuilder;
import kalaha.GameState;

import java.util.Random;




/**
 * Handles debugging.
 * Created by Nejc on 26. 09. 2016.
 */
public class Debugging {


    private GameState gameState;
    private Random random = new Random();




    public Debugging() {
        gameState = new GameState();
    }




    public static void main(String[] args) {
        //while (true) {
        System.out.println("Starting a new game...");
        Debugging d = new Debugging();
        d.run();
        //}
    }


    private static int getRandom() {
        return 1 + (int) (Math.random() * 6);
    }


    private void addText(String msg) {
        System.out.println(msg);
    }


    private int getOpponentMove(GameState currentBoard) {
        int selectedAmbo = getRandom();
        while (!currentBoard.moveIsPossible(selectedAmbo))
            selectedAmbo = getRandom();
        return selectedAmbo;
    }




    private void run() {
        boolean startWithAI = true;
        int ai = startWithAI ? 1 : 2;
        int opponent = (ai % 2) + 1;

        while (!gameState.gameEnded()) {
            if (gameState.getNextPlayer() == ai) {

                System.out.println("AI's move ... ");

                int move = getAIMove(gameState);

                gameState.makeMove(move);

            } else {

                int move = getOpponentMove(gameState);
                System.out.println("Opponent's turn ... "); // + move);
                gameState.makeMove(move);
            }

            this.addText("");
        }

        int winner = gameState.getWinner();
        if (winner >= 1) {
            System.out.println("Winner: " + (winner == ai ? "AI" : "Player"));
            System.out.println("---------------------");
            System.out.println("Score AI: " + gameState.getScore(ai));
            System.out.println("Score Opp: " + gameState.getScore(opponent));
            System.out.println("Score Diff: " + (gameState.getScore(winner) - gameState.getScore((winner % 2) + 1)));
        } else {
            System.out.println("Draw");
        }

    }




    private int getAIMove(GameState currentBoard) {
        TimedBuilder tb = TimedBuilder.buildFor(currentBoard, 5000);
        System.out.println("Depth: " + tb.getDepth());
        System.out.println("Util: " + tb.getUtility());
        return tb.getMove();
/*        TreeBuilder tb = new SimpleTreeBuilder(currentBoard);
        return tb.getBestMove(12);*/

//        AIClientManager clientManager = AIClientManager.create(currentBoard, 5000L);
////		DepthLevelSupplier depthLevelSupplier = StartArrayDepthLevelSupplier.create(5, 2, 3);
//        DepthLevelSupplier depthLevelSupplier = StartArrayDepthLevelSupplier.createNoLimit(2, 6, 4);
//
//        clientManager.run(depthLevelSupplier);
//
//        addText("Depth reached: " + clientManager.getDepthReached());
//        addText("Utility Value: " + clientManager.getRoot().getUtilityValue());
//
//        return clientManager.getSelectedMove();
    }


}

