package wumpusworld.qlearning;

import wumpusworld.World;

import java.util.Collection;

/**
 * Created by smarti on 14.10.16.
 */
public class State {
    private int x;
    private int y;
    private int direction;
    private boolean inPit;
    private boolean hasArrow;

    public State(int x, int y, int direction, boolean inPit, boolean hasArrow) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.inPit = inPit;
        this.hasArrow = hasArrow;
    }

    public State(World w){
        this.x = w.getPlayerX();
        this.y = w.getPlayerY();
        this.direction = w.getDirection();
        this.inPit = w.isInPit();
        this.hasArrow = w.hasArrow();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDirection() {
        return direction;
    }

    public boolean isInPit() {
        return inPit;
    }

    public boolean isHasArrow() {
        return hasArrow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        if (x != state.x) return false;
        if (y != state.y) return false;
        if (direction != state.direction) return false;
        if (inPit != state.inPit) return false;
        return hasArrow == state.hasArrow;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + direction;
        result = 31 * result + (inPit ? 1 : 0);
        result = 31 * result + (hasArrow ? 1 : 0);
        return result;
    }
}
