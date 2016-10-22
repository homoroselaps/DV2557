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
    private QTable<Double> q;
    private QTable<Integer> countTable = new QTable<>(0);
    private State currentState;
    private final double alpha;
    private final double gamma;
    private final double N = 5;

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
        this.rnd = new Random(42L);
        this.q = new QTable<>(0.0);
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
        ////System.out.println(currentState);
        ////stream(Action.values()).forEach(a -> System.out.println(a.toString() + ": " + q.getValue(currentState,a) + " " + countTable.getValue(currentState, a)));
        Action action = getNextAction();
        if(rnd.nextDouble()>0.9){
            action = Action.values()[rnd.nextInt(Action.values().length)];
        }
        ////System.out.println("action: " + action);
        // calculate utility value
        World nextWorld = action.makeAction(w);
        State nextState = new State(nextWorld);
        double oldUtil = q.getValue(currentState, action);
        double futureUtil = Arrays.stream(Action.values())
                .mapToDouble(a -> q.getValue(nextState, a))
                .max().getAsDouble();
        double newUtil = oldUtil + alpha *
                (getReward(nextWorld) + gamma * futureUtil - oldUtil);
        // update utility
        q.setValue(currentState, action, newUtil);
        // increase count of state action pair
        countTable.setValue(currentState, action, countTable.getValue(currentState, action)+1);
        // do action
        //System.out.println(currentState + " " +  action.toString() + " " + newUtil);
        w.doAction(action.getCommandName());
        currentState = nextState;
/*        if(action == Action.shoot){
            System.out.println("Shot arrow");
        }
        if(action == Action.grabGold){
            System.out.println("grabbed");
        }
        if(action == Action.climb){
            System.out.println("climbed");
        }*/
    }

    private double getReward(World nextWorld){
        return nextWorld.getScore() - w.getScore();
    }

    private Action getNextAction() {
        return stream(Action.values())
                .max(Comparator.comparingDouble(a -> {
                    if (countTable.getValue(currentState,a) < N) {
                        return 1000 + q.getValue(currentState, a);
                    }
                    else {
                        return q.getValue(currentState, a);
                    }
                }))
                .get();
    }

}
