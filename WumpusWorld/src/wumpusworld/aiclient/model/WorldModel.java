package wumpusworld.aiclient.model;


import wumpusworld.World;
import wumpusworld.aiclient.Action;
import wumpusworld.aiclient.Direction;
import wumpusworld.aiclient.util.Event;
import wumpusworld.aiclient.util.EventInterface;

import java.util.Objects;




/**
 * An internal model of the {@link World}, as perceived by a player. {@link Chunk}s' coordinates are zero-based.
 * <p>
 * Created by Nejc on 12. 10. 2016.
 */
public class WorldModel implements Cloneable {




    /**
     * A collection of all percepts.
     */
    protected final PerceptCollection[] percepts;
    private final World world;
    private final Event<Action> eventAction = new Event<>();
    private final Event<Chunk> eventChunkExplored = new Event<>();




    /**
     * Gets the collection of all percepts.
     *
     * @return The collection.
     */
    public PerceptCollection[] getPercepts() {
        return percepts.clone();
    }


    /**
     * Gets the event that fires when a player plays an action.
     *
     * @return Action event.1
     */
    public EventInterface<Action> getActionEvent() {
        return eventAction.getInterface();
    }


    /**
     * Gets the event that fires when a new {@link Chunk} is explored.
     *
     * @return Chunk explored event.
     */
    public EventInterface<Chunk> getChunkExploredEvent() {
        return eventChunkExplored.getInterface();
    }




    private WorldModel(World world, PerceptCollection[] percepts) {
        this.world = world;
        this.percepts = percepts;
    }


    /**
     * Creates a new instance of the {@link WorldModel} from given {@link World}.
     * Note that all actions should be called from this class not the world anymore. Otherwise this model may not work properly.
     *
     * @param world The world.
     */
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




    private boolean isValidPosition(int x, int y) {
        return !(x < 0
                || y < 0
                || x >= world.getSize()
                || y >= world.getSize());
    }


    /**
     * Checks if given location is valid (not out of bounds).
     *
     * @param location The location to test.
     * @return Whether or not given location is valid.
     */
    public boolean isValidPosition(Point location) {
        return isValidPosition(location.getX(), location.getY());
    }


    /**
     * Checks if given index is valid (not out of bounds).
     *
     * @param index The index to test.
     * @return Whether or not given index is valid.
     */
    protected boolean isValidPosition(int index) {
        return index >= 0 && index < (getSize() * getSize());
    }


    /**
     * Requires given location to be valid. Otherwise throws an {@link IndexOutOfBoundsException}.
     *
     * @param x The X coordinate of the location.
     * @param y The Y coordinate of the location.
     */
    protected void requireValidPosition(int x, int y) {
        if (!isValidPosition(x, y))
            throw new IndexOutOfBoundsException();
    }


    /**
     * Requires given location to be valid. Otherwise throws an {@link IndexOutOfBoundsException}.
     *
     * @param location The location to test.
     */
    protected void requireValidPosition(Point location) {
        if (!isValidPosition(location))
            throw new IndexOutOfBoundsException();
    }


    /**
     * Requires given index to be valid. Otherwise throws an {@link IndexOutOfBoundsException}.
     *
     * @param index The index to test.
     */
    protected void requireValidPosition(int index) {
        if (!isValidPosition(index))
            throw new IndexOutOfBoundsException();
    }



    /**
     * Converts given location to an index. The location must be valid.
     *
     * @param location The location. Must be valid.
     * @return The index.
     */
    protected int toIndex(Point location) {
        requireValidPosition(location);
        return location.toIndex(getSize());
    }


    /**
     * Converts given X and Y coordinates to a point. The coordinates must be valid.
     *
     * @param x The X coordinate. Must be valid.
     * @param y The Y coordinate. Must be valid.
     * @return The point.
     */
    protected Point toPoint(int x, int y) {
        requireValidPosition(x, y);
        return new Point(x, y);
    }


    /**
     * Converts give index to point. The index must be valid.
     *
     * @param index The index. Mus tbe valid.
     * @return THe points.
     */
    protected Point toPoint(int index) {
        requireValidPosition(index);
        int x = index % getSize();
        int y = index / getSize();
        return new Point(x, y);
    }




    /**
     * Gets the {@link PerceptCollection} at given location.
     *
     * @param location Location of the percept collection.1
     * @return The percept collection.
     */
    public PerceptCollection getPercepts(Point location) {
        requireValidPosition(location);
        return percepts[location.toIndex(getSize())];
    }


    /**
     * Gets the {@link Chunk} at given location.
     *
     * @param location The location.
     * @return A new instance of the Chunk. This always returns a NEW instance.
     */
    public Chunk getChunk(Point location) {
        requireValidPosition(location);
        return new Chunk(this, location);
    }



    /**
     * Gets the chunk at given index.
     *
     * @param index The index.
     * @return A new instance of the Chunk. This always returns a NEW instance.
     */
    public Chunk getChunk(int index) {
        return new Chunk(this, toPoint(index));
    }


    /**
     * Gets all chunks.
     *
     * @return An array of NEW instances of all chunks.
     */
    public Chunk[] getChunks() {
        Chunk[] cells = new Chunk[getSize() * getSize()];

        for (int i = 0; i < cells.length; i++) {
            cells[i] = getChunk(i);
        }

        return cells;
    }




    /**
     * Evaluates an actions and passes it to the underlying {@link World}.
     *
     * @param action The action.
     * @return Whether or not the action was successful.
     */
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


    /**
     * Gets the score.
     *
     * @return The score.
     */
    public int getScore() {
        return world.getScore();
    }


    /**
     * Gets the size of the word.
     *
     * @return The size. Width and height are the same.
     */
    public int getSize() {
        return world.getSize();
    }


    /**
     * Checks if the game is over.
     *
     * @return Whether or not the game is over.
     */
    public boolean isGameOver() {
        return world.gameOver();
    }


    /**
     * Checks if the chunk at location has already been visited.
     *
     * @param location The location to test.
     * @return Whether or not the chunk at given location has already been explored.
     */
    public boolean isVisited(Point location) {
        requireValidPosition(location);
        return world.isVisited(location.getX() + 1, location.getY() + 1);
    }


    /**
     * Checks if the chunk at given location has not been visited yet.
     *
     * @param location The location to test.
     * @return Whether or not the chunk has not been explored yet.
     */
    public boolean isUnknown(Point location) {
        return !isVisited(location);
    }


    /**
     * Checks if the player is located at given location.
     *
     * @param location The location to test.
     * @return Whether or not the player is at given location.
     */
    public boolean hasPlayer(Point location) {
        Objects.requireNonNull(location);
        return world.hasPlayer(location.getX() + 1, location.getY() + 1);
    }


    /**
     * Gets the player's location.
     *
     * @return The player's location.
     */
    public Point getPlayerLocation() {
        return new Point(world.getPlayerX() - 1, world.getPlayerY() - 1);
    }


    /**
     * Checks if the Wumpus is alive.
     *
     * @return Whether or not the Wumpus is alive.
     */
    public boolean isWumpusAlive() {
        return world.wumpusAlive();
    }


    /**
     * Checks if the player still has the arrow.
     *
     * @return Whether the player still has the arrow.
     */
    public boolean hasArrow() {
        return world.hasArrow();
    }


    /**
     * Checks if the player carries the gold treasure.
     *
     * @return Whether or not the player carries the gold treasure.
     */
    public boolean hasGold() {
        return world.hasGold();
    }


    /**
     * Gets the direction the player in which is facing.
     *
     * @return The direction the player in which is facing.
     */
    public Direction getPlayerDirection() {
        return Direction.fromLegacyDirection(world.getDirection());
    }


    /**
     * Checks if the player is in a pit.
     *
     * @return Whether or not the player is in a pit.
     */
    public boolean isInPit() {
        return world.isInPit();
    }


    /**
     * Checks if the player can move into a direction.
     *
     * @param direction The direction.
     * @return Whether the player can move into given direction.
     */
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




    /**
     * Creates a clone of the underlying {@link World}.
     *
     * @return The clone.
     */
    public World cloneWorld() {
        return world.cloneWorld();
    }


    /**
     * Clones this world model. Calls the {@link World#cloneWorld()} method of the underlying {@link World}.
     *
     * @return the clone.
     */
    @Override
    public Object clone() {
        PerceptCollection[] newPerceptCollection = new PerceptCollection[percepts.length];
        for (int i = 0; i < percepts.length; i++)
            newPerceptCollection[i] = (PerceptCollection) percepts[i].clone();
        return new WorldModel(world.cloneWorld(), newPerceptCollection);
    }


}