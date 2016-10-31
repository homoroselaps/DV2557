package wumpusworld.qlearning;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 *     Key for the table in {@link QTable}
 * </p>
 * <p>
 *     Stores the {@link State} and {@link Action} of an agent and can be serialized by the Jackson Json library
 * </p>
 *
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
        Pattern p = Pattern.compile("QKey\\{state=State\\{x=(\\d+),\\sy=(\\d+),\\sdirection=(\\d+)," +
                "\\swumpusAlive=(false|true),\\sinPit=(false|true)," +
                "\\shasArrow=(false|true)\\},\\saction=(\\w+)\\}");
        Matcher m = p.matcher(json);
        if(m.matches()){
            int x = Integer.parseInt(m.group(1));
            int y = Integer.parseInt(m.group(2));
            int dir = Integer.parseInt(m.group(3));
            boolean wumpusAlive = Boolean.parseBoolean(m.group(4));
            boolean inPit = Boolean.parseBoolean(m.group(5));
            boolean hasArrow = Boolean.parseBoolean(m.group(6));
            this.state = new State(x,y,dir, wumpusAlive, inPit,hasArrow);
            this.action = Action.valueOf(m.group(7));
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
