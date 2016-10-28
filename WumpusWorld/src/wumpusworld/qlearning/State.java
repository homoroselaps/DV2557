package wumpusworld.qlearning;

import wumpusworld.World;

/**
 * Created by smarti on 14.10.16.
 */
public class State {
    private int x;
    private int y;
    private int direction;
    private boolean wumpusAlive;
    private boolean inPit;
    private boolean hasArrow;

    public State(int x, int y, int direction, boolean wumpusAlive, boolean inPit, boolean hasArrow) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.wumpusAlive = wumpusAlive;
        this.inPit = inPit;
        this.hasArrow = hasArrow;
    }

    public State(World w){
        this.x = w.getPlayerX();
        this.y = w.getPlayerY();
        this.direction = w.getDirection();
        this.inPit = w.isInPit();
        this.wumpusAlive = w.wumpusAlive();
        this.hasArrow = w.hasArrow();
    }

    /**
     * Constructs State for the {@link World} world
     * @param world World after the action
     * @param lastWorld World before action
     * @param lastState State for lastWorld
     * @param action action
     */
    public State(World world, World lastWorld, State lastState, Action action){
        this.x = world.getPlayerX();
        this.y = world.getPlayerY();
        this.direction = world.getDirection();
        // in pit if already was in pit and did not climb out, or if just fallen in pit
        this.inPit = (lastState.isInPit() && !(action == Action.climb)) || world.isInPit();
        // alive if he was still alive before and was not just shot
        this.wumpusAlive = world.wumpusAlive() && lastState.isWumpusAlive();
        // arrow if there was an arrow before and if it was not just shot
        this.hasArrow = lastState.isHasArrow() && action != Action.shoot;
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

    public boolean isWumpusAlive() {
        return wumpusAlive;
    }

    public void setWumpusAlive(boolean wumpusAlive) {
        this.wumpusAlive = wumpusAlive;
    }

    public void setInPit(boolean inPit) {
        this.inPit = inPit;
    }

    public void setHasArrow(boolean hasArrow) {
        this.hasArrow = hasArrow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        if (x != state.x) return false;
        if (y != state.y) return false;
        if (direction != state.direction) return false;
        if (wumpusAlive != state.wumpusAlive) return false;
        if (inPit != state.inPit) return false;
        return hasArrow == state.hasArrow;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + direction;
        result = 31 * result + (wumpusAlive ? 1 : 0);
        result = 31 * result + (inPit ? 1 : 0);
        result = 31 * result + (hasArrow ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "State{" +
                "x=" + x +
                ", y=" + y +
                ", direction=" + direction +
                ", wumpusAlive=" + wumpusAlive +
                ", inPit=" + inPit +
                ", hasArrow=" + hasArrow +
                '}';
    }
}
