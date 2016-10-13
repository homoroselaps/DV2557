package wumpusworld.aiclient.model;


import wumpusworld.World;
import wumpusworld.aiclient.Action;
import wumpusworld.aiclient.util.Event;
import wumpusworld.aiclient.util.EventInterface;

import java.util.Objects;




/**
 * An internal model of the {@link World}, as perceived by a player. {@link Chunk}s' coordinates are zero-based.
 */
public class WorldModel implements Cloneable {




	protected final PerceptCollection[] percepts;
	private final World world;
	private final Event<Action> eventAction = new Event<>();




	public PerceptCollection[] getPercepts() {
		return percepts.clone();
	}


	public int getSize() {
		return world.getSize();
	}


	public EventInterface<Action> getActionEvent() {
		return eventAction.getInterface();
	}




	private WorldModel(World world, PerceptCollection[] percepts) {
		this.world = world;
		this.percepts = percepts;
	}


	public WorldModel(World world) {
		Objects.requireNonNull(world);
		this.world = world;

		int size = world.getSize();
		int count = size * size;
		percepts = new PerceptCollection[count];
		for (int i = 0; i < count; i++) {
			int x = (i % size) + 1;
			int y = (i / size) + 1;
			this.percepts[i] = PerceptCollection.fromWorld(world, x, y);
		}
	}




	public boolean isValidPosition(int x, int y) {
		return !(x < 0
				|| y < 0
				|| x >= world.getSize()
				|| y >= world.getSize());
	}


	public boolean isValidPosition(Point point) {
		return isValidPosition(point.getX(), point.getY());
	}


	protected boolean isValidPosition(int index) {
		return index >= 0 && index < (getSize() * getSize());
	}


	protected void requireValidPosition(int x, int y) {
		if (!isValidPosition(x, y))
			throw new IndexOutOfBoundsException();
	}


	protected void requireValidPosition(Point point) {
		if (!isValidPosition(point))
			throw new IndexOutOfBoundsException();
	}


	protected void requireValidPosition(int index) {
		if (!isValidPosition(index))
			throw new IndexOutOfBoundsException();
	}




	protected int toIndex(Point point) {
		requireValidPosition(point);
		return point.toIndex(getSize());
	}


	protected Point toPoint(int x, int y) {
		requireValidPosition(x, y);
		return new Point(x, y);
	}


	protected Point toPoint(int index) {
		requireValidPosition(index);
		int x = index & getSize();
		int y = index / getSize();
		return new Point(x, y);
	}




	public PerceptCollection getPercepts(Point point) {
		requireValidPosition(point);
		return percepts[point.toIndex(getSize())];
	}


	public Chunk getChunk(Point point) {
		requireValidPosition(point);
		return new Chunk(this, point);
	}


	public Chunk getChunk(int index) {
		return new Chunk(this, toPoint(index));
	}


	public Chunk[] getChunks() {
		Chunk[] cells = new Chunk[getSize() * getSize()];

		for (int i = 0; i < cells.length; i++) {
			cells[i] = getChunk(i);
		}

		return cells;
	}




	public boolean isVisited(Point point) {
		requireValidPosition(point);
		return world.isVisited(point.getX() + 1, point.getY() + 1);
	}


	public boolean isUnknown(Point point) {
		return !isVisited(point);
	}


	public boolean hasPlayer(Point point) {
		Objects.requireNonNull(point);
		return world.hasPlayer(point.getX() + 1, point.getY() + 1);
	}


	public boolean isInPit() {
		return world.isInPit();
	}


	public boolean hasArrow() {
		return world.hasArrow();
	}


	public boolean wumpusAlive() {
		return world.wumpusAlive();
	}


	public boolean hasGold() {
		return world.hasGold();
	}


	public int getDirection() {
		return world.getDirection();
	}




	public boolean doAction(Action action) {
		boolean res = world.doAction(action.getLegacyAction());
		if (res)
			eventAction.invoke(this, action);
		return res;
	}




	public World cloneWorld() {
		return world.cloneWorld();
	}


	@Override
	public Object clone() throws CloneNotSupportedException {
		return new WorldModel(world.cloneWorld(), percepts.clone());
	}


}