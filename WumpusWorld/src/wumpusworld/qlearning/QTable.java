package wumpusworld.qlearning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by smarti on 14.10.16.
 */
public class QTable {
    private HashMap<State, HashMap<Action, Double>> q = new HashMap<>();

    public QTable(){

    }

    public double getUtility(State state, Action action){
        double result = 0;
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
