package wumpusworld.qlearning;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by smarti on 15.10.16.
 */
public class QKey {
    private State state;
    private Action action;

    /**
     * This constructor is used by jackson to deserialize a json HashMap with QKey as key
     * @param json
     */
    public QKey(String json){
        Pattern p = Pattern.compile("QKey\\{state=State\\{x=(\\d+),\\sy=(\\d+)," +
                "\\sdirection=(\\d+),\\sinPit=(false|true),\\shasArrow=(false|true)\\}," +
                "\\saction=(\\w+)\\}");
        Matcher m = p.matcher(json);
        if(m.matches()){
            int x = Integer.parseInt(m.group(1));
            int y = Integer.parseInt(m.group(2));
            int dir = Integer.parseInt(m.group(3));
            boolean inPit = Boolean.parseBoolean(m.group(4));
            boolean hasArrow = Boolean.parseBoolean(m.group(5));
            this.state = new State(x,y,dir,inPit,hasArrow);
            this.action = Action.valueOf(m.group(6));
        } else {
            throw new IllegalArgumentException("json could not be parsed");
        }
    }

    public QKey(State state, Action action) {
        this.state = state;
        this.action = action;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QKey qKey = (QKey) o;

        if (state != null ? !state.equals(qKey.state) : qKey.state != null) return false;
        return action == qKey.action;

    }

    @Override
    public int hashCode() {
        int result = state != null ? state.hashCode() : 0;
        result = 31 * result + (action != null ? action.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "QKey{" +
                "state=" + state +
                ", action=" + action +
                '}';
    }
}
