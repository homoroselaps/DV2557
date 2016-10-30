package wumpusworld.qlearning;

import wumpusworld.*;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * Created by smarti on 15.10.16.
 */
public class LearningManager {

    public static void learn(String fileName, World world){
        learnUntilStop(fileName, world, 1500);
    }

    public static void learn(String fileName, int map){
        learnUntilStop(fileName, map, 1500);
    }

    public static void learnUntilStop(String fileName, int map, int times) {
        World world = new MapReader().readMaps().get(map).generateWorld();
        learnUntilStop(fileName, world, times);
    }

    public static void learnUntilStop(String fileName, World world, int times) {
        QTable<Double> q = new QTable<>(0.0);
        int lastScore = 0;
        for (int i = 0; i < times; ++i) {
            World startingWorld = world.cloneWorld();
            Agent a = new LearningAgent(startingWorld, q, new Random(42), 0.2, 0.7, 1.05 - (i+1)/(double)times, 10);
            while(!startingWorld.gameOver()) {
                a.doAction();
            }
            lastScore = startingWorld.getScore();
            q.writeTable(fileName);
        }
        System.out.println("GameOver. Score: " + lastScore);
    }

    public static void main(String args[]) {
        final long time = System.nanoTime();
        IntStream.range(0,7).forEach(mapNum-> {
                learnUntilStop("learnedTable" + mapNum + ".json", mapNum, 1500);
        });

        //int map = 4;
        //learnUntilStop("learnedTable"+map+".json", map, 1000);
    }
}
