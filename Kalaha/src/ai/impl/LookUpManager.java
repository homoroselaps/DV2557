/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
/**
 *
 * @author jt-1
 */
public class LookUpManager {
    private float[] firstMoveRatio = new float[6];
    private int bestMove = -1;
    public LookUpManager() {
    }
    public void loadFile(String filename) {
        if(!new File(filename).exists()) return;
        int[] gamesWon = new int[6];
        int[] gamesTotal = new int[6];
        try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();

            while (line != null) {
                try {
                int move = Integer.parseInt(""+line.charAt(0))-1;
                if (line.charAt(2) == '1')
                    gamesWon[move]++;
                gamesTotal[move]++;
                }
                catch (Exception ex) {
                    
                }
                finally {
                    line = br.readLine();    
                }
            }
            float bestRatio = -1;
            for (int i = 0; i < 6; i++) {
                firstMoveRatio[i] = (float)gamesWon[i] / (gamesTotal[i] + 1);
                if (firstMoveRatio[i] > bestRatio) {
                    bestRatio = firstMoveRatio[i];
                    bestMove = i+1;
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }       
    }
    
    public int getBestMove() {
        return bestMove;
    }
}
