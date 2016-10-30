package wumpusworld.qlearning;

import static org.junit.Assert.*;

/**
 * Created by smarti on 15.10.16.
 */
public class QTableTest {
    @org.junit.Test
    public void writeAndRead() throws Exception {
        QTable<Double> q = new QTable<>(0.0);
        State sampleState = new State(1, 1, 1, true, false, true);
        State sampleState1 = new State(1, 2, 1, true, false, true);
        State sampleState2 = new State(1, 1, 3, false, true, false);
        q.setValue(sampleState, Action.climb, 1.0);
        q.setValue(sampleState1, Action.climb, 2.0);
        q.setValue(sampleState2, Action.climb, 3.0);
        q.setValue(sampleState, Action.shoot, 4.0);
        q.setValue(sampleState1, Action.shoot, 5.0);
        q.setValue(sampleState2, Action.shoot, 6.0);
        q.setValue(sampleState, Action.move, 7.0);
        q.setValue(sampleState1, Action.move, 8.0);
        q.setValue(sampleState2, Action.move, 9.0);
        q.setValue(sampleState, Action.turnLeft, 7.1);
        q.setValue(sampleState1, Action.turnRight, 8.2);
        q.setValue(sampleState2, Action.grabGold, 9.3);
        q.writeTable("sampletable.json");
        QTable<Double> p = new QTable<>(0.0);
        p.readTable("sampletable.json");
        assertEquals(q,p);
    }

    public void testReward() throws Exception {

    }
}