package wumpusworld.qlearning;

import java.util.*;
import java.util.function.DoubleSupplier;

/**
 * Created by smarti on 14.10.16.
 */
public class QTable {
    private HashMap<State, HashMap<Action, Double>> q = new HashMap<>();
    private DoubleSupplier defaultUtility;

    public QTable(){
        defaultUtility = ()->0;
    }

    public QTable(double defaultUtility){
        this.defaultUtility = ()->defaultUtility;
    }

    public QTable(DoubleSupplier defaultUtility){
        this.defaultUtility = defaultUtility;
    }

    public double getUtility(State state, Action action){
        double result = defaultUtility.getAsDouble();
        //TODO maybe other default value than 0
        if(!q.containsKey(state)){
            q.put(state, new HashMap<>());
        } else {
            HashMap<Action, Double> map = q.get(state);
            if(map.containsKey(action)){
                result = map.get(action);
            }
        }
        return result;
    }

    public void setUtility(State state, Action action, double util){
        HashMap<Action, Double> map;
        if(!q.containsKey(state)){
            q.put(state, new HashMap<>());
        }
        map = q.get(state);
        map.put(action, util);
    }
}
