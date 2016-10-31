package wumpusworld;


import wumpusworld.aiclient.Action;
import wumpusworld.aiclient.Direction;
import wumpusworld.aiclient.assumptionmaking.AssumptionManager;
import wumpusworld.aiclient.model.Chunk;
import wumpusworld.aiclient.model.Point;
import wumpusworld.aiclient.model.WorldModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static wumpusworld.aiclient.Action.TURN_RIGHT;
import static wumpusworld.aiclient.model.TFUValue.*;


public class LogicAgent implements Agent {


    private final WorldModel worldModel;
    private final AssumptionManager assumptionManager;
    private ArrayList<Chunk> safeNeighbours = new ArrayList<>();
    private Point lastKnownLocation = new Point();


    public LogicAgent(World world) {
        this.worldModel = new WorldModel(world);
        this.assumptionManager = new AssumptionManager(this.worldModel);
        this.assumptionManager.init();
    }

    /**
     * Turns the player into the desired direction.
     *
     * @param playerDirection The desired direction to be turned to.
     */
    public void setRightDirection(Direction playerDirection) {
        Direction CurrentDirection = worldModel.getPlayerDirection();

        while (CurrentDirection != playerDirection) {

            worldModel.doAction(TURN_RIGHT);
            CurrentDirection  =worldModel.getPlayerDirection();
        }

    }

    /**
     * Discovers if any of our neighbours are safe to visit
     *
     * @param safeNeighbours List that gets filled with safe Chunks to visit
     * @param neighbours     Arrays of all the current neigbhours
     */
    public static void getSafeNeighbours(ArrayList<Chunk> safeNeighbours, Chunk[] neighbours) {
        for (int k = 0; k < neighbours.length; k++) {
            Chunk neighbour = neighbours[k];
            if ((neighbour.getPercepts().isSafe()) && !neighbour.isVisited()) {
                if (safeNeighbours.size() > 0 && !safeNeighbours.get(safeNeighbours.size() - 1).equals(neighbour)) {
                    continue;
                }
                safeNeighbours.add(neighbour);
            }
        }
    }


    /**
     * Discovers thing about Chunks on the map when there are  no more safe Chunks
     *
     * @param safeNeighbours  List that gets filled with safe Chunks to visit, in this case the one with the gold
     * @param unknownChunks   List that gets filled with Chunks that are unknown to us if they are safe or not
     * @param dangerousChunks List that gets filled with dangerous Chunks to visit
     */
    public void mapSearch(ArrayList<Chunk> safeNeighbours, ArrayList<Chunk> unknownChunks, ArrayList<Chunk> dangerousChunks) {
        for (int i = 0; i < worldModel.getSize(); i++) {
            for (int j = 0; j < worldModel.getSize(); j++) {
                Point temp = new Point(i, j);
                if (worldModel.getChunk(temp).getPercepts().getPit() == UNKNOWN && worldModel.getChunk(temp).getPercepts().getWumpus() == FALSE) {
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

    /**
     * In cases where we find a safe Chunk more than 1 square away from us,
     * this function provides us with index of neighbour where we will move
     * to get close to our final safe Chunk
     *
     * @param neighbours           Arrays of all the current neigbhours
     * @param finalSafeDestination Point of safe Chunk we are trying to reach
     * @param lastLocation         Point of our last location
     * @return
     */
    public static int getIndexOfNextNeighbour(Chunk[] neighbours, Point finalSafeDestination, Point lastLocation) {
        int m = 0;
        if (!neighbours[0].getPercepts().isSafe()) {
            m++;
        }
        for (int l = 0; l < neighbours.length; l++) {
            Chunk neighbour = neighbours[l];
            if (lastLocation.equals(neighbour.getLocation())) {
                continue;
            }
            if (neighbour.isVisited() && neighbour.getPercepts().isSafe()) {

                if (neighbour.getLocation().distance(finalSafeDestination) <= neighbours[m].getLocation().distance(finalSafeDestination)) {
                    m = l;
                } else if (lastLocation.equals(neighbours[m].getLocation())) {
                    m = l;
                }
            }
        }
        if (!neighbours[m].getPercepts().isSafe()) {
            for (int i = 0; i < neighbours.length; i++) {
                if (neighbours[i].getPercepts().getSafe() == UNKNOWN) {
                    m = i;
                    break;
                }
            }
        }
        return m;
    }

    /**
     * Funcation that searches through the Map to find safe Chunks which we shall visit and to find a Wumpus
     *
     * @param safeNeighbours List that gets filled with safe Chunks to visit
     * @return In case we will find Wumpus we will return his position, otherwise it will reamin null
     */
    public Point getSafeChunks(ArrayList<Chunk> safeNeighbours) {
        Point wumpusLocation = null;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Point temp = new Point(i, j);
                if (worldModel.getChunk(temp).getPercepts().isSafe() && !worldModel.isVisited(temp)) {
                    safeNeighbours.add(worldModel.getChunk(temp));
                    continue;
                }
                if (worldModel.isWumpusAlive()) {
                    if (worldModel.getChunk(temp).getPercepts().getWumpus() == TRUE) {
                        wumpusLocation = temp;
                    }
                }
            }
        }
        return wumpusLocation;
    }


    /**
     * Handles the logic and moving of our agent
     */
    public void doAction() {
        if (worldModel.isGameOver())
            return;

        ArrayList<Chunk> unknownChunks = new ArrayList<>();
        ArrayList<Chunk> dangerousChunks = new ArrayList<>();

        Point location = worldModel.getPlayerLocation();
        Point wumpusLocation = null;
        int indexOfNextNeighbour;
        if (worldModel.getScore() == 0) {
            lastKnownLocation = location;
        }
        if (worldModel.isInPit()) {
            worldModel.doAction(Action.CLIMB);
            return;
        }

        if (worldModel.getChunk(location).getPercepts().getGold() == TRUE) {
            worldModel.doAction(Action.GRAB);
            return;
        }

        Direction whereToGo;
        Chunk[] neighbours = worldModel.getChunk(location).getAdjacent();

        getSafeNeighbours(safeNeighbours, neighbours);


        /**
         * In case there will be no neighbours that are safe to visit, we will try to search them and also Wumpus
         */
        if (safeNeighbours.size() == 0) {
            wumpusLocation = getSafeChunks(safeNeighbours);
        }

        /**
         * In case that there will be safe Chunks that we found, we will try to visit them
         */
        if (safeNeighbours.size() > 0) {
            Chunk nextDestinationSafeNeighbour = safeNeighbours.remove(safeNeighbours.size() - 1);
            Point finalSafeDestination = nextDestinationSafeNeighbour.getLocation();
            Point vector = finalSafeDestination.translate(-location.getX(), -location.getY());
            double distance = location.distance(finalSafeDestination);

            /**
             * If the safe Chunk is our direct neighbour, than we will just set right direction and move
             * in case that it is further away, we will try to move to it
             * through shortest and safest way
             */
            if (distance <= 1) {
                whereToGo = Direction.setDirectionToGo(vector);
            } else {
                indexOfNextNeighbour = getIndexOfNextNeighbour(neighbours, finalSafeDestination, lastKnownLocation);
                vector = neighbours[indexOfNextNeighbour].getLocation().translate(-location.getX(), -location.getY());
                whereToGo = Direction.setDirectionToGo(vector);
            }
            setRightDirection(whereToGo);
            worldModel.doAction(Action.MOVE);
            lastKnownLocation = location;
            return;
        }
        /**
         * In case there are no more safe Chunks to visit and we know where the Wumpus is, we will shoot him
         */
        if (wumpusLocation != null && safeNeighbours.size() == 0 && worldModel.hasArrow()) {
            Point vectorToWump = wumpusLocation.translate(-location.getX(), -location.getY());
            if (location.getX() == wumpusLocation.getX() || location.getY() == wumpusLocation.getY()) {
                whereToGo = Direction.setDirectionToGo(vectorToWump);
                setRightDirection(whereToGo);
                worldModel.doAction(Action.SHOOT);
                return;
            } else {
                indexOfNextNeighbour = getIndexOfNextNeighbour(neighbours, wumpusLocation, lastKnownLocation);
                vectorToWump = neighbours[indexOfNextNeighbour].getLocation().translate(-location.getX(), -location.getY());
                whereToGo = Direction.setDirectionToGo(vectorToWump);
                setRightDirection(whereToGo);
                worldModel.doAction(Action.MOVE);
                lastKnownLocation = location;
                return;
            }
        }

        /**
         * If we start the game and there is stench in Point(0,0) we will try shooting in right direction
         * and with that we will either hit Wumpus, or we will be sure where it is
         */
        if (worldModel.getChunk(location).getPercepts().getStench() == TRUE && location.getX() == 0 && location.getY() == 0 && worldModel.hasArrow() == true) {
            worldModel.doAction(Action.SHOOT);
            return;
        }


        mapSearch(safeNeighbours, unknownChunks, dangerousChunks);
        /**
        * We are sorting an List of Unknown Chunks, so we can visit the closest
        */
        Comparator<Chunk> comp = (Chunk a, Chunk b) -> b.compareDistances(a);
        Collections.sort(unknownChunks, comp);

        /**
         * If we are without the safe Chunks to explore, we will just have to select
         * a Chunk about which we don't know if it is safe or not
         */
        if (safeNeighbours.size() == 0) {
            if (unknownChunks.size() > 0) {
                safeNeighbours.add(unknownChunks.remove(0));
            } else if (unknownChunks.size() == 0 && dangerousChunks.size() > 0) {
                safeNeighbours.add(dangerousChunks.remove(dangerousChunks.size() - 1));
            }
        }

        /**
         * Recursive calling of doAction() so we can visit our selected Chunk
         */
        doAction();


    }


}
