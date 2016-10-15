package wumpusworld.qlearning;

import wumpusworld.Agent;
import wumpusworld.MapReader;
import wumpusworld.World;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.function.BiFunction;

import static java.util.Arrays.stream;

/**
 * Created by smarti on 14.10.16.
 */
public class LearningAgent implements Agent{

    private World w;
    private Random rnd;
    private QTable q;
    private State currentState;
    private final double alpha;
    private final double gamma;
    private static BiFunction<World, Action, Double> rewardFinder = (world, action) -> {
        double result = -1;
        switch (action) {
            case grabGold:
                if(world.hasGlitter(world.getPlayerX(), world.getPlayerY())){
                    result += 1000;
                }
                break;
            case shoot:
                result -= 10;
                break;
        }
        return result;
    };

    public LearningAgent(World world, QTable q, Random rnd, double alpha, double gamma){
        this.w = world;
        this.rnd = rnd;
        this.q = q;
        this.currentState = new State(world);
        this.alpha = alpha;
        this.gamma = gamma;
    }

    public LearningAgent(World world, double alpha, double gamma){
        this.w = world;
        this.rnd = new Random(42l);
        this.q = new QTable(0);
        this.currentState = new State(world);
        this.alpha = alpha;
        this.gamma = gamma;
    }

    public QTable getQ() {
        return q;
    }

    @Override
    public void doAction() {
        // find best action
        Action action = stream(Action.values())
                .max(Comparator.comparingDouble(a -> q.getUtility(currentState,a)))
                .get();
        // update utility value
        World nextWorld = action.makeAction(w);
        State nextState = new State(nextWorld);
        double oldUtil = q.getUtility(currentState, action);
        double futureUtil = Arrays.stream(Action.values())
                .mapToDouble(action1 -> q.getUtility(nextState, action1))
                .max().getAsDouble();
        double newUtil = oldUtil + alpha *
                (rewardFinder.apply(w,action) + gamma * futureUtil - oldUtil);
        q.setUtility(currentState, action, newUtil);
        // do action
        //System.out.println(currentState + " " +  action.toString() + " " + newUtil);
        w.doAction(action.getCommandName());
        currentState = nextState;
    }

    public static void main(String args[]){
        World w = new MapReader().readMaps().get(0).generateWorld();
        stream(Action.values()).map(a -> a.toString() + ": " + rewardFinder.apply(w, a))
                .forEach(System.out::println);
    }
}
