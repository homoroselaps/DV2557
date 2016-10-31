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

import static wumpusworld.aiclient.model.TFUValue.*;




public class LogicAgent implements Agent {




    private final WorldModel worldModel;
    private final AssumptionManager assumptionManager;
    private ArrayList<Chunk> safeNeighbours = new ArrayList<>();
    private Point lastKnownLocation = new Point();




    public LogicAgent(World world) {
        this.worldModel = new WorldModel(world);
        this.assumptionManager = new AssumptionManager(this.worldModel);
        this.assumptionManager.initAll();
    }




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

        if (safeNeighbours.size() == 0) {
            wumpusLocation = neighbours[0].getWorldModel().getSafeChunks(safeNeighbours);
        } else if (safeNeighbours.size() > 0) {
            Chunk nextDestinationSafeNeighbour = safeNeighbours.remove(safeNeighbours.size() - 1);
            Point finalSafeDestination = nextDestinationSafeNeighbour.getLocation();
            Point vector = finalSafeDestination.translate(-location.getX(), -location.getY());
            double distance = location.distance(finalSafeDestination);

            if (distance <= 1) {
                whereToGo = Direction.setDirectionToGo(vector);
            } else {
                indexOfNextNeighbour = getIndexOfNextNeighbour(neighbours, finalSafeDestination, lastKnownLocation);
                vector = neighbours[indexOfNextNeighbour].getLocation().translate(-location.getX(), -location.getY());
                whereToGo = Direction.setDirectionToGo(vector);
            }
            worldModel.setRightDirection(whereToGo);
            worldModel.doAction(Action.MOVE);
            lastKnownLocation = location;
            return;
        }

        if (wumpusLocation != null && safeNeighbours.size() == 0 && worldModel.hasArrow()) {
            Point vectorToWump = wumpusLocation.translate(-location.getX(), -location.getY());
            if (location.getX() == wumpusLocation.getX() || location.getY() == wumpusLocation.getY()) {
                whereToGo = Direction.setDirectionToGo(vectorToWump);
                worldModel.setRightDirection(whereToGo);
                worldModel.doAction(Action.SHOOT);
                return;
            } else {
                indexOfNextNeighbour = getIndexOfNextNeighbour(neighbours, wumpusLocation, lastKnownLocation);
                vectorToWump = neighbours[indexOfNextNeighbour].getLocation().translate(-location.getX(), -location.getY());
                whereToGo = Direction.setDirectionToGo(vectorToWump);
                worldModel.setRightDirection(whereToGo);
                worldModel.doAction(Action.MOVE);
                lastKnownLocation = location;
                return;
            }
        }

        if (worldModel.getChunk(location).getPercepts().getStench() == TRUE && location.getX() == 0 && location.getY() == 0 && worldModel.hasArrow() == true) {
            worldModel.doAction(Action.SHOOT);
            return;
        }


        mapSearch(safeNeighbours, unknownChunks, dangerousChunks);


        Comparator<Chunk> comp = (Chunk a, Chunk b) -> b.compareDistances(a);
        Collections.sort(unknownChunks, comp);

        if (safeNeighbours.size() == 0) {
            if (unknownChunks.size() > 0) {
                safeNeighbours.add(unknownChunks.remove(0));
            } else if (unknownChunks.size() == 0 && dangerousChunks.size() > 0) {
                safeNeighbours.add(dangerousChunks.remove(dangerousChunks.size() - 1));
            }
        }


        doAction();


    }


}
