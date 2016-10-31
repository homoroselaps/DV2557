package wumpusworld.aiclient.model;


import wumpusworld.aiclient.Percept;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;




/**
 * Represents a chunk/cell/location on/in a {@link WorldModel}.
 * <p>
 * Created by Nejc on 12. 10. 2016.
 */
public class Chunk {




    private final Point location;
    private final WorldModel worldModel;




    /**
     * Gets associated {@link WorldModel}.
     *
     * @return Associated {@link WorldModel}.
     */
    public WorldModel getWorldModel() {
        return worldModel;
    }


    /**
     * Gets the location of the this chunk.
     *
     * @return Location of this chunk.
     */
    public Point getLocation() {
        return location;
    }


    /**
     * Gets the percepts in this chunk.
     *
     * @return A {@link PerceptCollection} of all percept in this chunk.
     */
    public PerceptCollection getPercepts() {
        return worldModel.getPercepts(location);
    }


    /**
     * Checks if this chunk has been visited by the player.
     *
     * @return Whether or not this chunk has been visited by the player.
     */
    public boolean isVisited() {
        return worldModel.isVisited(location);
    }




    /**
     * Creates a new instance of the {@link Chunk}.
     *
     * @param worldModel Associated {@link WorldModel}.
     * @param location   Location of this chunk.
     */
    public Chunk(WorldModel worldModel, Point location) {
        Objects.requireNonNull(worldModel);
        if (!worldModel.isValidPosition(location))
            throw new IllegalArgumentException();

        this.worldModel = worldModel;
        this.location = location;
    }




    /**
     * Gets all adjacent chunks.
     *
     * @return An array of all adjacent chunks of associated {@link WorldModel}.
     */
    public Chunk[] getAdjacent() {
        int x = location.getX();
        int y = location.getY();
        int size = worldModel.getSize();

        ArrayList<Chunk> adjacent = new ArrayList<>(4);
        if (x > 0)
            adjacent.add(new Chunk(worldModel, location.translate(-1, 0)));
        if (x < size - 1)
            adjacent.add(new Chunk(worldModel, location.translate(1, 0)));
        if (y > 0)
            adjacent.add(new Chunk(worldModel, location.translate(0, -1)));
        if (y < size - 1)
            adjacent.add(new Chunk(worldModel, location.translate(0, 1)));

        return adjacent.toArray(new Chunk[adjacent.size()]);
    }


    /**
     * Checks whether given chunk is adjacent to this chunk.
     *
     * @param chunk Chunk to test.
     * @return Whether gor not given chunk is adjacent to this chunk.
     */
    public boolean isAdjacent(Chunk chunk) {
        Objects.requireNonNull(chunk);
        int dx = Math.abs(this.location.getX() - chunk.location.getX());
        int dy = Math.abs(this.location.getY() - chunk.location.getY());
        return (dx ^ dy) == 1 && (dx | dy) == 1;
    }


    /**
     * Gets the only adjacent chunk, which satisfies a {@link Percept}.
     *
     * @param percept Percept to be satisfied.
     * @return The only chunk (if such even exists) that satisfied the given percept.
     */
    public Optional<Chunk> getOnlyAdjacentChunkWithSatisfiablePercept(Percept percept) {
        Objects.requireNonNull(percept);

        Chunk only = null;
        for (Chunk chunk : getAdjacent()) {
            if (chunk.getPercepts().getPercept(percept).isSatisfiable()) {
                if (only == null) only = chunk;
                else return Optional.empty();
            }
        }

        return Optional.ofNullable(only);
    }


    /**
     * Compares distances to player's location.
     *
     * @param second Chunk to compare.
     * @return The value 0 if chunks are equally distant from player's location; a value less than 0 if this chunk is closer to player's location; and a value greater than 0 if given chunk is closer to player's location.
     */
    public int compareDistances(Chunk second) {
        double distance1 = worldModel.getPlayerLocation().distance(this.getLocation());
        double distance2 = worldModel.getPlayerLocation().distance(second.getLocation());
        return Double.compare(distance2, distance1);
    }




    /**
     * Gets the hash code.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return location.hashCode() ^ worldModel.hashCode();
    }


    /**
     * Checks if the other object is a chunk that is a part of the same {@link WorldModel} and lies at the same location.
     *
     * @param obj Other object to compare.
     * @return Whether the given object is a chunk that is a part of the same {@link WorldModel} and lies at the same location.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Chunk))
            return false;

        Chunk chunk = (Chunk) obj;
        return this.worldModel == chunk.worldModel
                && this.location.equals(chunk.location);

    }


    /**
     * Gets the string representation of this chunk.
     *
     * @return The string representation of this chunk.
     */
    @Override
    public String toString() {
        return "Chunk at " + location.toString();
    }


}