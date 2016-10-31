package wumpusworld.qlearning;

import wumpusworld.Agent;
import wumpusworld.MapReader;
import wumpusworld.World;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.function.BiFunction;

import static java.util.Arrays.stream;
import static java.util.Arrays.stream;

/**
 * Agent that uses Q-Learning
 * Created by smarti on 14.10.16.
 */
public class LearningAgent implements Agent {

    private World w;
    private Random rnd;
    private QTable<Double> q;
    /**
     * stores how often an action was executed in a state
     */
    private QTable<Integer> countTable = new QTable<>(0);
    private State currentState;
    private final double alpha;
    private final double gamma;
    /**
     * number of times each action get's tried out
     */
    private int explorationCount = 0;
    /**
     * probability of random action
     */
    private double epsilon;

    /**
     * Constructor with all necessary parameters
     * @param world
     * @param q
     * @param rnd
     * @param alpha
     * @param gamma
     * @param epsilon
     * @param exploration
     */
    public LearningAgent(World world, QTable q, Random rnd, double alpha, double gamma, double epsilon, int exploration) {
        this.w = world;
        this.rnd = rnd;
        this.q = q;
        this.currentState = new State(world);
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.explorationCount = exploration;
    }

    public QTable getQ() {
        return q;
    }

    @Override
    public void doAction() {
        // find best action
        Action action = getNextAction();
        // calculate utility value
        w.doAction(action.getCommandName());
        State nextState = new State(w);
        double oldUtil = q.getValue(currentState, action);
        double futureUtil = Arrays.stream(Action.values())
            .mapToDouble(a -> q.getValue(nextState, a))
            .max().getAsDouble();
        double newUtil = oldUtil + alpha
            * (getReward(w, action) + gamma * futureUtil - oldUtil);
        // update utility
        q.setValue(currentState, action, newUtil);
        // increase count of state action pair
        countTable.setValue(currentState, action, countTable.getValue(currentState, action) + 1);
        // do action
        currentState = nextState;
    }

    /**
     * returns reward according to score change
     * @param nextWorld
     * @param action
     * @return
     */
    private double getReward(World nextWorld, Action action) {
        double result = -1;
        switch (action) {
            case grabGold:
                if(nextWorld.hasGold()){
                    result += 1000;
                }
                break;
            case shoot:
                result -= 10;
                break;
            case move:
                if(nextWorld.hasWumpus(nextWorld.getPlayerX(), nextWorld.getPlayerY())){
                    result -= 1000;
                }
                if(nextWorld.hasPit(nextWorld.getPlayerX(), nextWorld.getPlayerY())){
                    result -= 1000;
                }
                break;
        }
        return result;
    }

    /**
     * @return next action with probability of epsilon a random action, otherwise best one from the qtable
     */
    private Action getNextAction() {
        if (rnd.nextDouble() < epsilon) {
            return Action.values()[rnd.nextInt(Action.values().length)];
        }
        return stream(Action.values())
            .max(Comparator.comparingDouble(a -> {
                if (countTable.getValue(currentState, a) < explorationCount) {
                    // if not tried out often increase value
                    return 1000 + q.getValue(currentState, a);
                } else {
                    return q.getValue(currentState, a);
                }
            }))
            .get();
    }

}
