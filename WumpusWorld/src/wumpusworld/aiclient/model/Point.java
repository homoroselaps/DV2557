package wumpusworld.aiclient.model;


/**
 * An immutable 2-dimensional point.
 * Created by Nejc on 12. 10. 2016.
 */
public class Point implements Cloneable {




	private final int x;
	private final int y;




	public int getX() {
		return x;
	}


	public int getY() {
		return y;
	}




	public Point() {
		this.x = 0;
		this.y = 0;
	}


	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}


	public Point(Point point) {
		this.x = point.x;
		this.y = point.y;
	}




	public Point translate(int dx, int dy) {
		return new Point(x + dx, y + dy);
	}


	public int toIndex(int width) {
		return (y * width) + x;
	}




	@Override
	public int hashCode() {
		return (x << 16) ^ y;
	}


	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Point))
			return false;

		Point other = (Point) obj;
		return this.x == other.x
				&& this.y == other.y;
	}


	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Point(this);
	}


	@Override
	public String toString() {
		return "{ X = " + x + ", Y = " + y + " }";
	}


}
