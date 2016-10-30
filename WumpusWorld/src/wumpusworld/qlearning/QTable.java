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
 * Class that wraps a HashMap to store the QTable and the Counttable
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

    /**
     * write to json file
     * @param fileName
     */
    public void writeTable(String fileName){
        ObjectMapper om = new ObjectMapper();
        try {
            om.writeValue(new File(fileName), q);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * read from json file
     * @param fileName
     */
    public void readTable(String fileName){
        File f = new File(fileName);
        if(!f.exists() || f.isDirectory()) return;
        ObjectMapper om = new ObjectMapper();
        try {
            TypeFactory typeFactory = om.getTypeFactory();
            MapType mapType = typeFactory.constructMapType(HashMap.class, QKey.class, Double.class);

            this.q = om.readValue(f, mapType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * retrieve stored value from table
     * @param state the state
     * @param action the action
     * @return the stored value or a default value
     */
    public T getValue(State state, Action action){
        T result = defaultUtility.get();
        //TODO maybe other default value than 0
        if(q.containsKey(new QKey(state,action))){
            result = q.get(new QKey(state,action));
        }
        return result;
    }

    /**
     * update stored value
     * @param state the state
     * @param action the action
     * @param util the value that should be stored
     */
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
