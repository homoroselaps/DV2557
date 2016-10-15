package wumpusworld.qlearning;

import wumpusworld.Agent;
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
    private BiFunction<World, Action, Double> rewardFinder
            = (world, action) -> (double) world.getScore() - action.makeAction(world).getScore();

    public LearningAgent(World world, QTable q, Random rnd, double alpha, double gamma){
        this.w = world;
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
                .max(Comparator.comparingDouble(a -> rewardFinder.apply(w,a)))
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
        w.doAction(action.getCommandName());
        currentState = nextState;
        w = nextWorld;
    }
}
