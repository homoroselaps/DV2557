package wumpusworld.aiclient.model;


import wumpusworld.aiclient.Percept;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static wumpusworld.aiclient.model.TFUValue.FALSE;
import static wumpusworld.aiclient.model.TFUValue.TRUE;
import static wumpusworld.aiclient.model.TFUValue.UNKNOWN;


/**
 * A chunk of {@link WorldModel}.
 * Created by Nejc on 12. 10. 2016.
 */
public class Chunk {


    private final Point point;
    private final WorldModel worldModel;


    public WorldModel getWorldModel() {
        return worldModel;
    }


    public Point getLocation() {
        return point;
    }


    public PerceptCollection getPercepts() {
        return worldModel.getPercepts(point);
    }


    public Chunk(WorldModel worldModel, Point point) {
        Objects.requireNonNull(worldModel);
        if (!worldModel.isValidPosition(point))
            throw new IllegalArgumentException();

        this.worldModel = worldModel;
        this.point = point;
    }


    public Chunk[] getAdjacent() {
        int x = point.getX();
        int y = point.getY();
        int size = worldModel.getSize();

        ArrayList<Chunk> adjacent = new ArrayList<>(4);
        if (x > 0)
            adjacent.add(new Chunk(worldModel, point.translate(-1, 0)));
        if (x < size - 1)
            adjacent.add(new Chunk(worldModel, point.translate(1, 0)));
        if (y > 0)
            adjacent.add(new Chunk(worldModel, point.translate(0, -1)));
        if (y < size - 1)
            adjacent.add(new Chunk(worldModel, point.translate(0, 1)));

        return adjacent.toArray(new Chunk[adjacent.size()]);
    }


    public boolean isAdjacent(Chunk chunk) {
        Objects.requireNonNull(chunk);
        int dx = Math.abs(this.point.getX() - chunk.point.getX());
        int dy = Math.abs(this.point.getY() - chunk.point.getY());
        return (dx ^ dy) == 1 && (dx | dy) == 1;
    }


    public Optional<Chunk> getOnlyChunkWithSatisfiablePercept(Percept percept) {
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

    public int getIndexOfNextNeighour(Chunk[] neighbours, Point finalSafeDestination, Point lastLocation) {
        int m = 0;
        if (neighbours[0].getPercepts().isSafe() == false) {
            m++;
        }

        for (int l = 0; l < neighbours.length; l++) {
            if (lastLocation.getX() == neighbours[l].getLocation().getX() && lastLocation.getY() == neighbours[l].getLocation().getY()) {
                continue;
            }
            if (worldModel.isVisited(neighbours[l].getLocation()) == true && neighbours[l].getPercepts().isSafe() == true) {

                if (neighbours[l].getLocation().distance(finalSafeDestination) <= neighbours[m].getLocation().distance(finalSafeDestination)) {
                    m = l;
                } else if (lastLocation.getX() == neighbours[m].getLocation().getX() && lastLocation.getY() == neighbours[m].getLocation().getY()) {
                        m = l;
                }
            }
        }
        if(neighbours[m].getPercepts().isSafe() == false){
            for(int i = 0; i<neighbours.length;i++){
                if (neighbours[i].getPercepts().getSafe() == UNKNOWN){
                    m=i;
                    break;
                }
            }
        }
        return m;
    }

    public Point getSafeChunks(ArrayList<Chunk> safeNeighbours) {
        Point wumpusLocation = null;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Point temp = new Point(i, j);
                if (worldModel.getChunk(temp).getPercepts().isSafe() == true && worldModel.isVisited(temp) == false) {
                    safeNeighbours.add(worldModel.getChunk(temp));
                    continue;
                }
                if (worldModel.isWumpusAlive() == true) {
                    if (worldModel.getChunk(temp).getPercepts().getWumpus() == TRUE) {
                        wumpusLocation = temp;
                    }
                }

            }
        }
        return wumpusLocation;
    }

    public void mapSearch(ArrayList<Chunk> safeNeighbours, ArrayList<Chunk> unknownChunks, ArrayList<Chunk> dangerousChunks) {

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Point temp = new Point(i, j);
                if (worldModel.getChunk(temp).getPercepts().getPit() == UNKNOWN && worldModel.getChunk(temp).getPercepts().getWumpus() == FALSE  ) {
                    unknownChunks.add(worldModel.getChunk(temp));
                } else if (worldModel.getChunk(temp).getPercepts().getGold() == TRUE) {
                    safeNeighbours.add(worldModel.getChunk(temp));
                    break;
                } else if (worldModel.getChunk(temp).getPercepts().getSafe() == FALSE) {
                    dangerousChunks.add(worldModel.getChunk(temp));
                }
            }
        }
    }


    public void getSafeNeighoubrsToAnArray(ArrayList<Chunk> safeNeighbours, Chunk[] neighbours) {
        for (int k = 0; k < neighbours.length; k++) {
            if ((neighbours[k].getPercepts().isSafe() == true) && (worldModel.isVisited(neighbours[k].getLocation()) == false)) {
                if (safeNeighbours.size() > 0) {
                    if (safeNeighbours.get(safeNeighbours.size() - 1) != neighbours[k]) {
                        continue;
                    }
                }
                safeNeighbours.add(neighbours[k]);
            }
        }
    }


    @Override
    public int hashCode() {
        return point.hashCode() ^ worldModel.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Chunk))
            return false;

        Chunk chunk = (Chunk) obj;
        return this.worldModel == chunk.worldModel
                && this.point.equals(chunk.point);

    }


    @Override
    public String toString() {
        return "Chunk at " + point.toString();
    }


    public int compareTo(Chunk second) {

        double distance1 = worldModel.getPlayerLocation().distance(this.getLocation());
        double distance2 = worldModel.getPlayerLocation().distance(second.getLocation());
        return Double.compare(distance2, distance1);

    }


}