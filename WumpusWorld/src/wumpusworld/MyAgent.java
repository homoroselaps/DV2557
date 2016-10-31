package wumpusworld;

import jdk.nashorn.internal.objects.annotations.Where;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import wumpusworld.aiclient.Action;
import wumpusworld.aiclient.Direction;
import wumpusworld.aiclient.assumptionmaking.AssumptionManager;
import wumpusworld.aiclient.model.*;
import wumpusworld.aiclient.model.WorldModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static wumpusworld.aiclient.model.TFUValue.*;

public class MyAgent implements Agent {
    private final WorldModel worldModel;
    private final AssumptionManager assumptionManager;
    private ArrayList<Chunk> safeNeighbours = new ArrayList<Chunk>();
    private ArrayList<Chunk> unknownChunks = new ArrayList<Chunk>();
    private ArrayList<Chunk> dangerousChunks = new ArrayList<Chunk>();
    private Point lastKnownLocation = new Point();

    public MyAgent(World world) {
        this.worldModel = new WorldModel(world);
        this.assumptionManager = new AssumptionManager(this.worldModel);
        this.assumptionManager.initAll();
    }

    public void doAction() {

        unknownChunks.clear();
        dangerousChunks.clear();
        Point location = worldModel.getPlayerLocation();
        Point wumpusLocation = null;
        int indexOfNextNeighour = 0;
        if (worldModel.getScore() == 0) {
            lastKnownLocation = location;
        }
        if (worldModel.isInPit() == true) {
            worldModel.doAction(Action.CLIMB);
            return;
        }

        if (worldModel.getChunk(location).getPercepts().getGold() == TRUE) {
            worldModel.doAction(Action.GRAB);
            return;
        }

        Direction whereToGo = Direction.UP;
        Chunk[] neighbours = worldModel.getChunk(location).getAdjacent();

        neighbours[0].getSafeNeighoubrsToAnArray(safeNeighbours, neighbours);

        if (safeNeighbours.size() == 0) {
            wumpusLocation = neighbours[0].getSafeChunks(safeNeighbours);
        }

        if (safeNeighbours.size() > 0) {


            Chunk nextDestinationSafeNeighbour = safeNeighbours.remove(safeNeighbours.size() - 1);
            Point finalSafeDestination = nextDestinationSafeNeighbour.getLocation();
            Point vector = finalSafeDestination.translate(-location.getX(), -location.getY());
            double distance = location.distance(finalSafeDestination);

            if (distance <= 1) {
                whereToGo = whereToGo.setDirectionToGo(location, vector);
            } else {
                indexOfNextNeighour = neighbours[indexOfNextNeighour].getIndexOfNextNeighour(neighbours, finalSafeDestination, lastKnownLocation);
                vector = neighbours[indexOfNextNeighour].getLocation().translate(-location.getX(), -location.getY());
                whereToGo = whereToGo.setDirectionToGo(location, vector);
            }
            worldModel.setRightDirection(whereToGo);
            worldModel.doAction(Action.MOVE);
            lastKnownLocation = location;
            return;
        }

        if (wumpusLocation != null && safeNeighbours.size() == 0 && worldModel.hasArrow() == true) {
            Point vectorToWump = wumpusLocation.translate(-location.getX(), -location.getY());
            if (location.getX() == wumpusLocation.getX() || location.getY() == wumpusLocation.getY()) {
                whereToGo = whereToGo.setDirectionToGo(location, vectorToWump);
                worldModel.setRightDirection(whereToGo);
                worldModel.doAction(Action.SHOOT);
                return;
            } else {
                indexOfNextNeighour = neighbours[indexOfNextNeighour].getIndexOfNextNeighour(neighbours, wumpusLocation, lastKnownLocation);
                vectorToWump = neighbours[indexOfNextNeighour].getLocation().translate(-location.getX(), -location.getY());
                whereToGo = whereToGo.setDirectionToGo(location, vectorToWump);
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


        neighbours[0].mapSearch(safeNeighbours, unknownChunks, dangerousChunks);


        Comparator<Chunk> comp = (Chunk a, Chunk b) -> {
            return b.compareTo(a);
        };

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
