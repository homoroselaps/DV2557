package wumpusworld.aiclient.model;


import wumpusworld.World;
import wumpusworld.aiclient.Action;
import wumpusworld.aiclient.Direction;
import wumpusworld.aiclient.util.Event;
import wumpusworld.aiclient.util.EventInterface;

import java.util.ArrayList;
import java.util.Objects;

import static wumpusworld.aiclient.model.TFUValue.TRUE;




/**
 * An internal model of the {@link World}, as perceived by a player. {@link Chunk}s' coordinates are zero-based.
 */
public class WorldModel implements Cloneable {




    protected final PerceptCollection[] percepts;
    private final World world;
    private final Event<Action> eventAction = new Event<>();
    private final Event<Chunk> eventChunkExplored = new Event<>();




    public PerceptCollection[] getPercepts() {
        return percepts.clone();
    }


    public EventInterface<Action> getActionEvent() {
        return eventAction.getInterface();
    }


    public EventInterface<Chunk> getChunkExploredEvent() {
        return eventChunkExplored.getInterface();
    }




    private WorldModel(World world, PerceptCollection[] percepts) {
        this.world = world;
        this.percepts = percepts;
    }


    public WorldModel(World world) {
        Objects.requireNonNull(world);
        this.world = world;

        int size = world.getSize();
        int count = size * size;
        percepts = new PerceptCollection[count];
        for (int i = 0; i < count; i++) {
            int x = (i % size);
            int y = (i / size);
            this.percepts[i] = PerceptCollection.fromWorld(world, x + 1, y + 1);
        }
    }




    public boolean isValidPosition(int x, int y) {
        return !(x < 0
                || y < 0
                || x >= world.getSize()
                || y >= world.getSize());
    }


    public boolean isValidPosition(Point location) {
        return isValidPosition(location.getX(), location.getY());
    }


    protected boolean isValidPosition(int index) {
        return index >= 0 && index < (getSize() * getSize());
    }


    protected void requireValidPosition(int x, int y) {
        if (!isValidPosition(x, y))
            throw new IndexOutOfBoundsException();
    }


    protected void requireValidPosition(Point location) {
        if (!isValidPosition(location))
            throw new IndexOutOfBoundsException();
    }


    protected void requireValidPosition(int index) {
        if (!isValidPosition(index))
            throw new IndexOutOfBoundsException();
    }




    protected int toIndex(Point location) {
        requireValidPosition(location);
        return location.toIndex(getSize());
    }


    protected Point toPoint(int x, int y) {
        requireValidPosition(x, y);
        return new Point(x, y);
    }


    protected Point toPoint(int index) {
        requireValidPosition(index);
        int x = index % getSize();
        int y = index / getSize();
        return new Point(x, y);
    }




    public PerceptCollection getPercepts(Point location) {
        requireValidPosition(location);
        return percepts[location.toIndex(getSize())];
    }


    public Chunk getChunk(Point location) {
        requireValidPosition(location);
        return new Chunk(this, location);
    }


    public void setRightDirection(Direction PlannedDirection) {
        Direction CurrentDirection = getPlayerDirection();

        while (CurrentDirection != PlannedDirection) {

            this.doAction(Action.TURN_RIGHT);
            CurrentDirection = getPlayerDirection();
        }

    }


    public Chunk getChunk(int index) {
        return new Chunk(this, toPoint(index));
    }


    public Chunk[] getChunks() {
        Chunk[] cells = new Chunk[getSize() * getSize()];

        for (int i = 0; i < cells.length; i++) {
            cells[i] = getChunk(i);
        }

        return cells;
    }




    public boolean doAction(Action action) {
        boolean res = world.doAction(action.getLegacyAction());
        boolean canMove = action == Action.MOVE && res;
        Point newLoc = null;

        if (canMove) {
            newLoc = getPlayerLocation();
            getPercepts(newLoc).copyFrom(PerceptCollection.fromWorld(world, newLoc.getX() + 1, newLoc.getY() + 1));
        }

        if (res) {
            eventAction.invoke(this, action);
            if (canMove)
                eventChunkExplored.invoke(this, getChunk(newLoc));
        }

        return res;
    }


    public int getScore() {
        return world.getScore();
    }



    public int getSize() {
        return world.getSize();
    }


    public boolean isGameOver() {
        return world.gameOver();
    }


    public boolean isVisited(Point location) {
        requireValidPosition(location);
        return world.isVisited(location.getX() + 1, location.getY() + 1);
    }


    public boolean isUnknown(Point location) {
        return !isVisited(location);
    }


    public boolean hasPlayer(Point location) {
        Objects.requireNonNull(location);
        return world.hasPlayer(location.getX() + 1, location.getY() + 1);
    }


    public Point getPlayerLocation() {
        return new Point(world.getPlayerX() - 1, world.getPlayerY() - 1);
    }


    public boolean isWumpusAlive() {
        return world.wumpusAlive();
    }


    public boolean hasArrow() {
        return world.hasArrow();
    }


    public boolean hasGold() {
        return world.hasGold();
    }


    public Direction getPlayerDirection() {
        return Direction.fromLegacyDirection(world.getDirection());
    }


    public boolean isInPit() {
        return world.isInPit();
    }




    public boolean canMove(Direction direction) {
        Point loc = getPlayerLocation();
        switch (direction) {
            case DOWN:
                return loc.getY() > 0;
            case UP:
                return loc.getY() < getSize() - 1;
            case LEFT:
                return loc.getX() > 0;
            case RIGHT:
                return loc.getX() < getSize() - 1;
            default:
                throw new IllegalArgumentException();
        }
    }


    public Point getSafeChunks(ArrayList<Chunk> safeNeighbours) {
        Point wumpusLocation = null;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Point temp = new Point(i, j);
                if (getChunk(temp).getPercepts().isSafe() && !isVisited(temp)) {
                    safeNeighbours.add(getChunk(temp));
                    continue;
                }
                if (isWumpusAlive()) {
                    if (getChunk(temp).getPercepts().getWumpus() == TRUE) {
                        wumpusLocation = temp;
                    }
                }

            }
        }
        return wumpusLocation;
    }





    public World cloneWorld() {
        return world.cloneWorld();
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        PerceptCollection[] newPerceptCollection = new PerceptCollection[percepts.length];
        for (int i = 0; i < percepts.length; i++)
            newPerceptCollection[i] = (PerceptCollection) percepts[i].clone();
        return new WorldModel(world.cloneWorld(), newPerceptCollection);
    }


}