package wumpusworld.qlearning;

import wumpusworld.MapReader;
import wumpusworld.World;

import static org.junit.Assert.*;

/**
 * Created by smarti on 28.10.16.
 */
public class StateTest {

    /**
     * Tests if for any two first actions the states are handled correctly, even without cloneing
     * @throws CloneNotSupportedException
     */
    @org.junit.Test
    public void testRemovedCloning() throws CloneNotSupportedException {
        World world = getSomeWorld();
        State old = new State(world);
        for(Action action1: Action.values()){
            for(Action action2: Action.values()){
                // with clone
                World nextClone = world.clone();
                nextClone.doAction(action1.getCommandName());
                State stateFromClone1 = new State(nextClone);
                nextClone = nextClone.clone();
                nextClone.doAction(action2.getCommandName());
                State stateFromClone2 = new State(nextClone);
                // without clone
                World nextNoClone1 = world.cloneWorld();
                nextNoClone1.doAction(action1.getCommandName());
                State stateNoClone1 = new State(nextNoClone1, world, old, action1);
                World nextNoClone2 = nextNoClone1.cloneWorld();
                nextNoClone2.doAction(action2.getCommandName());
                State stateNoClone2 = new State(nextNoClone2, nextNoClone1, stateNoClone1, action2);
                // clone and non clone should be equal
                assertEquals(stateFromClone1, stateNoClone1);
                assertEquals(stateFromClone2, stateNoClone2);
            }
        }
    }

    private World getSomeWorld(){
        return new MapReader().readMaps().get(0).generateWorld();
    }
}