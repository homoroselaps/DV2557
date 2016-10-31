package wumpusworld.aiclient;


import wumpusworld.aiclient.model.Point;




/**
 * An enum of direction. A more convenient representation than the one used by {@link wumpusworld.World}.
 * <p>
 * Created by Nejc on 13. 10. 2016.
 */
public enum Direction {


    /**
     * Facing up.
     */
    UP,
    /**
     * Facing right.
     */
    RIGHT,
    /**
     * Facing down.
     */
    DOWN,
    /**
     * Facing left.
     */
    LEFT;


    /**
     * Converts this value to the one used by {@link wumpusworld.World}.
     *
     * @return The value.
     */
    public int toLegacyDirection() {
        return this.ordinal();
    }


    /**
     * Converts a direction value used by {@link wumpusworld.World} to an {@link Action} enum value.
     *
     * @param direction The direction.
     * @return The {@link Direction} enum value.
     */
    public static Direction fromLegacyDirection(int direction) {
        return Direction.values()[direction];
    }


    /**
     * Gets the direction from given vector.
     *
     * @param fakeVectorPoint Direction unit vector. This parameter is NOT a point but a unit vector.
     * @return The direction given vector is facing or RIGHT of vector is (0,0).
     */
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


}
