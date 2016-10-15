package wumpusworld.qlearning;

import wumpusworld.*;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Created by smarti on 15.10.16.
 */
public class LearningManager {

    public static void learnUntilStop(String fileName, int map, int times) {
        QTable q = new QTable();
        for(int i=0; i<times; i++){
            World world = new MapReader().readMaps().get(map).generateWorld();
            Agent a = new LearningAgent(world, q, new Random(42), 0.2, 0.5);
            while(!world.gameOver()) {
                a.doAction();
            }
            if(world.gameOver()){
                System.out.println("GameOver. Score: " + world.getScore());
            }
        }
        q.writeTable(fileName);
    }

    public static void main(String args[]) {
        final long time = System.nanoTime();
        learnUntilStop("learnedTable5.json", 5, 1000);
    }
}
