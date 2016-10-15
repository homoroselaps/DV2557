package wumpusworld.qlearning;

import static org.junit.Assert.*;

/**
 * Created by smarti on 15.10.16.
 */
public class QTableTest {
    @org.junit.Test
    public void writeAndRead() throws Exception {
        QTable q = new QTable();
        State sampleState = new State(1, 1, 1, false, true);
        State sampleState1 = new State(1, 2, 1, false, true);
        State sampleState2 = new State(1, 1, 3, false, true);
        q.setUtility(sampleState, Action.climb, 1.0);
        q.setUtility(sampleState1, Action.climb, 2.0);
        q.setUtility(sampleState2, Action.climb, 3.0);
        q.setUtility(sampleState, Action.shoot, 4.0);
        q.setUtility(sampleState1, Action.shoot, 5.0);
        q.setUtility(sampleState2, Action.shoot, 6.0);
        q.setUtility(sampleState, Action.walk, 7.0);
        q.setUtility(sampleState1, Action.walk, 8.0);
        q.setUtility(sampleState2, Action.walk, 9.0);
        q.setUtility(sampleState, Action.turnLeft, 7.1);
        q.setUtility(sampleState1, Action.turnRight, 8.2);
        q.setUtility(sampleState2, Action.grabGold, 9.3);
        q.writeTable("sampletable.json");
        QTable p = new QTable();
        p.readTable("sampletable.json");
        assertEquals(q,p);
    }
}