package wumpusworld.aiclient;


import wumpusworld.aiclient.model.Point;

/**
 * Represents a direction the player is facing.
 * Created by Nejc on 13. 10. 2016.
 */
public enum Direction {


    UP,
    RIGHT,
    DOWN,
    LEFT;


    public static Direction fromLegacyDirection(int direction) {
        return Direction.values()[direction];
    }


    public int toLegacyDirection() {
        return this.ordinal();
    }


    public Direction setDirectionToGo(Point currentPoint, Point fakeVectorPoint) {
        Direction WhereToGo = Direction.RIGHT;

        if (fakeVectorPoint.getX() > 0) {
            WhereToGo = Direction.RIGHT;
        } else if (fakeVectorPoint.getX() < 0) {
            WhereToGo = Direction.LEFT;
        } else if (fakeVectorPoint.getY() < 0) {
            WhereToGo = Direction.DOWN;
        } else if (fakeVectorPoint.getY() > 0) {
            WhereToGo = Direction.UP;
        }

        return WhereToGo;
    }


}
