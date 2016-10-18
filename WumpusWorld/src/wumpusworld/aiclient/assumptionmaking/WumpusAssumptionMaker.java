package wumpusworld.aiclient.assumptionmaking;


import wumpusworld.aiclient.Action;
import wumpusworld.aiclient.model.Chunk;
import wumpusworld.aiclient.model.PerceptChanged;
import wumpusworld.aiclient.model.WorldModel;
import wumpusworld.aiclient.util.EventHandler;
import wumpusworld.aiclient.util.EventInterface;

import java.util.*;

import static wumpusworld.aiclient.Action.SHOOT;
import static wumpusworld.aiclient.Percept.WUMPUS;
import static wumpusworld.aiclient.model.TFUValue.FALSE;
import static wumpusworld.aiclient.model.TFUValue.TRUE;




/**
 * Makes assumptions on where Wumpus is located.
 * Created by Nejc on 14. 10. 2016.
 */
public class WumpusAssumptionMaker implements AssumptionMaker {




	private final WorldModel worldModel;
	private HashMap<EventHandler<?>, EventInterface<?>> listeners;
	private boolean done;
	private boolean wumpusAlive;
	private int chunksWithNoWumpus;




	public WorldModel getWorldModel() {
		return worldModel;
	}


	@Override
	public boolean isDone() {
		return done;
	}




	public WumpusAssumptionMaker(WorldModel worldModel) {
		Objects.requireNonNull(worldModel);
		this.worldModel = worldModel;
	}




	@Override
	public void init() {
		updateAll();
	}


	@Override
	public void updateAll() {
		done = false;
		subscribe();

		initInvokeWumpusKilled();
		if (done) return;
		initWumpusLocated();
		if (done) return;
		initChunkWithNoStenchDiscovered();
		if (done) return;
		initChunkWithStenchDiscovered();
		if (done) return;
		initChunkWithStenchHasOnlyOneAdjacentChunkWithWumpusLeft();
		if (done) return;
		initOnlyOneChunkWithWumpusRemaining();
	}


	@Override
	public void dispose() {
		unsubscribe();
	}


	private void subscribe() {
		if (listeners != null)
			unsubscribe();

		Chunk[] chunks = worldModel.getChunks();
		listeners = new HashMap<>(2 + chunks.length);

		EventHandler<Action> actionEventHandler = (sender, arg) -> onAction(arg);
		worldModel.getActionEvent().subscribe(actionEventHandler);
		listeners.put(actionEventHandler, worldModel.getActionEvent());

		EventHandler<Chunk> chunkExploredEventHandler = (sender, arg) -> onChunkExplored(arg);
		worldModel.getChunkExploredEvent().subscribe(chunkExploredEventHandler);
		listeners.put(chunkExploredEventHandler, worldModel.getChunkExploredEvent());

		for (final Chunk chunk : worldModel.getChunks()) {
			EventHandler<PerceptChanged> perceptChangedEventHandler = (sender, arg) -> onPerceptChanged(chunk, arg);
			chunk.getPercepts().getPerceptChangedEvent().subscribe(perceptChangedEventHandler);
		}
	}


	private void unsubscribe() {
		if (listeners != null)
			listeners.entrySet().forEach(pair -> pair.getValue().unsubscribe(pair.getKey()));
		this.listeners = null; // let GC do its work
	}


	private void onPreDone() {
		unsubscribe();
	}


	private void onDone() {
		unsubscribe();
		done = true;
	}




	private void onAction(Action action) {
		if (action == SHOOT) {
			invokeWumpusKilled();
		}
	}


	private void onChunkExplored(Chunk chunk) {
		invokeWumpusLocated(chunk);
		invokeChunkWithNoStenchDiscovered(chunk);
		invokeChunkWithStenchDiscovered(chunk);
		invokeChunkWithStenchHasOnlyOneAdjacentChunkWithWumpusLeft(chunk, true);
		invokeOnlyOneChunkWithWumpusRemaining();
	}


	private void onPerceptChanged(Chunk chunk, PerceptChanged perceptChanged) {
		if (done) return;

		if (perceptChanged.getPercept() == WUMPUS) {
			invokeWumpusLocated(chunk);
		}
	}




	private void initInvokeWumpusKilled() {
		this.wumpusAlive = true;
		invokeWumpusKilled();
	}


	private void initWumpusLocated() {
		for (Chunk chunk : worldModel.getChunks()) {
			if (chunk.getPercepts().getWumpus() == TRUE) {
				onWumpusLocated(chunk);
				return;
			}
		}
	}


	private void initChunkWithNoStenchDiscovered() {
		Arrays.stream(worldModel.getChunks())
				.filter(chunk -> chunk.getPercepts().getStench() == FALSE)
				.forEach(this::invokeChunkWithNoStenchDiscovered);
	}


	private void initChunkWithStenchDiscovered() {
		Arrays.stream(worldModel.getChunks())
				.filter(chunk -> chunk.getPercepts().getStench() == TRUE)
				.forEach(this::invokeChunkWithStenchDiscovered);
	}


	private void initChunkWithStenchHasOnlyOneAdjacentChunkWithWumpusLeft() {
		Arrays.stream(worldModel.getChunks())
				.filter(chunk -> chunk.getPercepts().getStench() == TRUE)
				.forEach(chunk -> invokeChunkWithStenchHasOnlyOneAdjacentChunkWithWumpusLeft(chunk, true));
	}


	private void initOnlyOneChunkWithWumpusRemaining() {
		invokeOnlyOneChunkWithWumpusRemaining();
	}




	private void invokeWumpusKilled() {
		boolean wumpusAlive = worldModel.isWumpusAlive();
		if (wumpusAlive != this.wumpusAlive) {
			if (wumpusAlive) throw new IllegalStateException("Wumpus added in mid-game.");
			this.wumpusAlive = false;
			onWumpusKilled();
		}
	}


	private void invokeWumpusLocated(Chunk chunk) {
		if (chunk.getPercepts().getWumpus() == TRUE)
			onWumpusLocated(chunk);
	}


	private void invokeChunkWithNoStenchDiscovered(Chunk chunk) {
		if (chunk.getPercepts().getStench() == FALSE)
			onChunkWithNoStenchDiscovered(chunk);
	}


	private void invokeChunkWithStenchDiscovered(Chunk chunk) {
		if (chunk.getPercepts().getStench() == TRUE)
			onChunkWithStenchDiscovered(chunk);
	}


	private void invokeChunkWithStenchHasOnlyOneAdjacentChunkWithWumpusLeft(Chunk chunk, boolean subscribe) {
		if (chunk.getPercepts().getStench() != TRUE) return;

		Optional<Chunk> theOnlyOne = chunk.getOnlyChunkWithSatisfiablePercept(WUMPUS);
		if (theOnlyOne.isPresent()) {
			onChunkWithStenchHasOnlyOneAdjacentChunkWithWumpusLeft(chunk, theOnlyOne.get());
		} else if (subscribe) {
			Arrays.stream(chunk.getAdjacent())
					.filter(c -> c.getPercepts().getStench().isSatisfiable())
					.forEach(c -> {
						EventHandler<PerceptChanged> perceptChangedEventHandler = (sender, arg) -> {
							if (arg.getPercept() == WUMPUS && arg.getNewValue() == FALSE)
								invokeChunkWithStenchHasOnlyOneAdjacentChunkWithWumpusLeft(chunk, false);
						};
						c.getPercepts().getPerceptChangedEvent().subscribe(perceptChangedEventHandler);
						listeners.put(perceptChangedEventHandler, c.getPercepts().getPerceptChangedEvent());
					});
		}
	}


	private void invokeOnlyOneChunkWithWumpusRemaining() {
		Chunk onlyWithWumpus = null;
		for (Chunk chunk : worldModel.getChunks()) {
			if (chunk.getPercepts().getWumpus().isSatisfiable()) {
				if (onlyWithWumpus == null)
					onlyWithWumpus = chunk;
				else return;
			}
		}

		if (onlyWithWumpus != null)
			onOnlyOneChunkWithWumpusRemaining(onlyWithWumpus);
	}




	private void onWumpusKilled() {
		onPreDone();
		Arrays.stream(worldModel.getChunks())
				.forEach(chunk -> chunk.getPercepts().setWumpus(FALSE));
		onDone();
	}


	private void onWumpusLocated(Chunk chunk) {
		onPreDone();
		Arrays.stream(worldModel.getChunks())
				.filter(c -> !c.equals(chunk))
				.forEach(c -> c.getPercepts().setWumpus(FALSE));
		onDone();
	}


	private void onChunkWithNoStenchDiscovered(Chunk chunk) {
		Arrays.stream(chunk.getAdjacent())
				.forEach(c -> c.getPercepts().setWumpus(FALSE));
	}


	private void onChunkWithStenchDiscovered(Chunk chunk) {
		Arrays.stream(worldModel.getChunks())
				.filter(c -> !c.isAdjacent(chunk))
				.forEach(c -> c.getPercepts().setWumpus(FALSE));
	}


	private void onChunkWithStenchHasOnlyOneAdjacentChunkWithWumpusLeft(Chunk chunk, Chunk adjacentChunk) {
		onPreDone();
		Arrays.stream(worldModel.getChunks())
				.filter(c -> !c.equals(chunk))
				.forEach(c -> c.getPercepts().setWumpus(FALSE));
		adjacentChunk.getPercepts().setWumpus();
		onDone();
	}


	private void onOnlyOneChunkWithWumpusRemaining(Chunk chunk) {
		onPreDone();
		chunk.getPercepts().setWumpus();
		onDone();
	}


}
