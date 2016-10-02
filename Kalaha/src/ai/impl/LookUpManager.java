/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.impl;

/**
 *
 * @author jt-1
 */
public class LookUpManager {
    private float[] firstMoveRatio = new float[6];
    private int bestMove = -1;
    public LookUpManager() {
    }
    
    public void loadFromStringArray(String[] input) {
        int[] gamesWon = new int[6];
        int[] gamesTotal = new int[6];
        for (String line : input) {
            try {
                int move = Integer.parseInt(""+line.charAt(0))-1;
                if (line.charAt(2) == '1')
                    gamesWon[move]++;
                gamesTotal[move]++;
            }
            catch (Exception ex) { }
        }
        float bestRatio = -1;
        for (int i = 0; i < 6; i++) {
            firstMoveRatio[i] = (float)gamesWon[i] / (gamesTotal[i] + 1);
            if (firstMoveRatio[i] > bestRatio) {
                bestRatio = firstMoveRatio[i];
                bestMove = i+1;
            }
        }
    }
    
    public int getBestMove() {
        return bestMove;
    }
    
    public static final String[] DATA = {
    // FirstMove ; <Won=1, Lost=0>
    "6;1",
    "6;1",
    "5;0",
    "6;1",
    "1;1",
    "4;1",
    "5;0",
    "6;1",
    "6;1",
    "1;1",
    "3;0",
    "4;1",
    "3;0",
    "3;0",
    "6;1",
    "1;1",
    "3;0",
    "4;1",
    "4;1",
    "5;0",
    "4;1",
    "1;1",
    "4;1",
    "5;0",
    "2;1",
    "4;1",
    "3;0",
    "4;1",
    "3;0",
    "6;1",
    "2;1",
    "6;1",
    "1;1",
    "1;1",
    "4;1",
    "4;1",
    "4;1",
    "1;1",
    "4;1",
    "1;1",
    "1;1",
    "5;0",
    "6;1",
    "1;1",
    "3;0",
    "6;1",
    "1;1",
    "2;1",
    "6;1",
    "2;1",
    "6;1",
    "6;1",
    "2;1",
    "6;1",
    "5;0",
    "3;0",
    "5;0",
    "2;1",
    "2;1",
    "5;0",
    "4;1",
    "1;1",
    "3;0",
    "5;0",
    "5;0",
    "5;0",
    "5;0",
    "2;1",
    "4;1",
    "3;0",
    "6;1",
    "3;0",
    "4;1",
    "6;1",
    "3;0",
    "3;0",
    "5;0",
    "5;0",
    "3;0",
    "6;1",
    "3;0",
    "2;1",
    "3;0",
    "1;1",
    "6;1",
    "3;0",
    "4;1",
    "6;1",
    "2;1",
    "6;1",
    "6;1",
    "6;1",
    "3;0",
    "2;1",
    "2;1",
    "5;0",
    "1;1",
    "2;1",
    "5;0",
    "4;1"
    };
}
