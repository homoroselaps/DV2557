package wumpusworld.aiclient.model;


import wumpusworld.aiclient.Percept;
import wumpusworld.aiclient.util.Event;
import wumpusworld.aiclient.util.EventInterface;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;




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
			if (!chunk.getPercepts().getPercept(percept).isSatisfiable()) {
				if (only == null) only = chunk;
				else return Optional.empty();
			}
		}

		return Optional.ofNullable(only);
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


}
