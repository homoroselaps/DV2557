package wumpusworld.aiclient.model;


/**
 * An immutable 2-dimensional point.
 * <p>
 * Created by Nejc on 12. 10. 2016.
 */
public class Point implements Cloneable {




    private final int x;
    private final int y;




    /**
     * Gets the X coordinate.
     *
     * @return The X coordinate.
     */
    public int getX() {
        return x;
    }


    /**
     * Gets the X coordinate.
     *
     * @return The X coordinate.
     */
    public int getY() {
        return y;
    }




    /**
     * Creates a new instance of the {@link Point} in the origin (0,0).
     */
    public Point() {
        this.x = 0;
        this.y = 0;
    }


    /**
     * Creates a new instance of the {@link Point} with given coordinates.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }


    /**
     * Creates a new instance of the {@link Point} which is a copy of given point.
     *
     * @param point Point to copy.
     */
    public Point(Point point) {
        this.x = point.x;
        this.y = point.y;
    }




    /**
     * Creates a new point that is translated for given measurements.
     *
     * @param dx Translation on the X axis.
     * @param dy Translation on the Y axit.
     * @return A new point.
     */
    public Point translate(int dx, int dy) {
        return new Point(x + dx, y + dy);
    }


    /**
     * Converts this point to an index, that represents a consecutive point in Yth row and Xth column.
     *
     * @param width Width of the map.
     * @return The index.
     */
    public int toIndex(int width) {
        return (y * width) + x;
    }


    /**
     * Calculates the distance between points.
     *
     * @param second The point to calculate distance to.
     * @return The distnace.
     */
    public double distance(Point second) {
        return Math.sqrt((second.getX() - x) * (second.getX() - x) + ((second.getY() - y) * (second.getY() - y)));
    }




    /**
     * Gets the hash code.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return (x << 16) ^ y;
    }


    /**
     * Checks if two points have the same X and Y coordinates.
     *
     * @param obj Point to test.
     * @return Whether or not given object is a point with the same coordinates as this point.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point))
            return false;

        Point other = (Point) obj;
        return this.x == other.x
                && this.y == other.y;
    }


    /**
     * Clones this point.
     *
     * @return A new point.
     */
    @Override
    protected Object clone() {
        return new Point(this);
    }


    /**
     * Gets the string representation of this point.
     *
     * @return The string representation of this point.
     */
    @Override
    public String toString() {
        return "{ X = " + x + ", Y = " + y + " }";
    }


}
