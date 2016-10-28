package wumpusworld.qlearning;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * Created by smarti on 14.10.16.
 */
public class QTable<T> {
    private HashMap<QKey, T> q = new HashMap<>();
    private Supplier<T> defaultUtility;

    public QTable(T defaultUtility){
        this.defaultUtility = ()->defaultUtility;
    }

    public QTable(Supplier<T> defaultUtility){
        this.defaultUtility = defaultUtility;
    }

    public void writeTable(String fileName){
        ObjectMapper om = new ObjectMapper();
        try {
            om.writeValue(new File(fileName), q);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readTable(String fileName){
        ObjectMapper om = new ObjectMapper();
        try {
            TypeFactory typeFactory = om.getTypeFactory();
            MapType mapType = typeFactory.constructMapType(HashMap.class, QKey.class, Double.class);

            this.q = om.readValue(new File(fileName), mapType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public T getValue(State state, Action action){
        T result = defaultUtility.get();
        //TODO maybe other default value than 0
        if(q.containsKey(new QKey(state,action))){
            result = q.get(new QKey(state,action));
        }
        return result;
    }

    public void setValue(State state, Action action, T util){
        q.put(new QKey(state, action), util);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QTable qTable = (QTable) o;

        return q != null ? q.equals(qTable.q) : qTable.q == null;

    }

    @Override
    public int hashCode() {
        return q != null ? q.hashCode() : 0;
    }
}