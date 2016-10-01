package ai.impl.tree;

import kalaha.GameState;
import sun.java2d.pipe.SpanShapeRenderer;

import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Created by smarti on 01.10.16.
 */
public class TimedBuilder {

    private int move;
    private int utility;
    private int depth;

    public TimedBuilder(int move, int utility, int depth) {
        this.move = move;
        this.utility = utility;
        this.depth = depth;
    }

    public int getMove() {
        return move;
    }

    public int getUtility() {
        return utility;
    }

    public int getDepth() {
        return depth;
    }

    /**
     * builds trees
     * @param game current {@link GameState}
     * @param milliSeconds time
     * @return best move
     */
    public static TimedBuilder buildFor(GameState game, long milliSeconds) {
        long startingTime = System.nanoTime();
        TimedBuilder move = null;
        int depth = 3;
        while (true){
            final int currentDepth = depth;
            final GameState gameState = game;
            Callable<TimedBuilder> runner = new Callable<TimedBuilder>() {
                @Override
                public TimedBuilder call() throws Exception {
                    SimpleTreeBuilder stb = new SimpleTreeBuilder(gameState);
                    int move = stb.getBestMove(currentDepth);
                    int depth = stb.getLastDepth();
                    int util = stb.getLastUtil();
                    return new TimedBuilder(move, util, depth);
                }
            };
            ExecutorService exe = Executors.newSingleThreadExecutor();
            Future<TimedBuilder> futRes = exe.submit(runner);
            try {
                TimedBuilder result = futRes.get(timeLeft(startingTime, milliSeconds), TimeUnit.MILLISECONDS);
                move = result;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                break;
            }
            depth += 2;
        }
        return move;
    }

    private static long timeLeft(long startNano, long limitMillis){
        return limitMillis - ((System.nanoTime()-startNano)/1000);
    }

}
