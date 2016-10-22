package wumpusworld.qlearning;

import wumpusworld.*;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Created by smarti on 15.10.16.
 */
public class LearningManager {

    public static void learnUntilStop(String fileName, int map, int times) {
        QTable<Double> q = new QTable<>(0.0);
        IntStream.range(0,times).forEach(i -> {
            World world = new MapReader().readMaps().get(map).generateWorld();
            Agent a = new LearningAgent(world, q, new Random(42), 0.2, 0.7);
            while(!world.gameOver()) {
                a.doAction();
            }
            if(world.gameOver()){
                System.out.println("GameOver. Score: " + world.getScore());
            }
            q.writeTable(fileName);
        });
    }

    public static void main(String args[]) {
        final long time = System.nanoTime();
        IntStream.range(0,7).forEach(mapNum-> {
                learnUntilStop("learnedTable" + mapNum + ".json", mapNum, 1000);
        });

        //int map = 4;
        //learnUntilStop("learnedTable"+map+".json", map, 1000);
    }
}
