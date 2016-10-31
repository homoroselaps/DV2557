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


    public static Direction setDirectionToGo(Point fakeVectorPoint) {
        Direction whereToGo = Direction.RIGHT;

        if (fakeVectorPoint.getX() > 0) {
            whereToGo = Direction.RIGHT;
        } else if (fakeVectorPoint.getX() < 0) {
            whereToGo = Direction.LEFT;
        } else if (fakeVectorPoint.getY() < 0) {
            whereToGo = Direction.DOWN;
        } else if (fakeVectorPoint.getY() > 0) {
            whereToGo = Direction.UP;
        }

        return whereToGo;
    }


    public int toLegacyDirection() {
        return this.ordinal();
    }


}
