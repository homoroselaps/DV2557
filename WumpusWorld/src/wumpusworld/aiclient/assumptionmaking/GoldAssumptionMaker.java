package wumpusworld.aiclient.assumptionmaking;


import wumpusworld.aiclient.Action;
import wumpusworld.aiclient.model.Chunk;
import wumpusworld.aiclient.model.PerceptChanged;
import wumpusworld.aiclient.model.WorldModel;
import wumpusworld.aiclient.util.EventHandler;
import wumpusworld.aiclient.util.EventInterface;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;


import static wumpusworld.aiclient.Percept.GOLD;
import static wumpusworld.aiclient.Percept.WUMPUS;
import static wumpusworld.aiclient.model.TFUValue.*;




/**
 * Makes assumptions on where gold is located.
 * Created by Nejc on 17. 10. 2016.
 */
public class GoldAssumptionMaker implements AssumptionMaker {




	private final WorldModel worldModel;
	private HashMap<EventHandler<?>, EventInterface<?>> listeners;
	private boolean done;
	private boolean goldLocated;
	private boolean hasGold;




	public WorldModel getWorldModel() {
		return worldModel;
	}


	@Override
	public boolean isDone() {
		return done;
	}


	public boolean isGoldLocated() {
		return goldLocated;
	}




	public GoldAssumptionMaker(WorldModel worldModel) {
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
		goldLocated = false;
		subscribe();

		initGoldPickedUp();
		if (done) return;
		initGoldLocated();
		if (done) return;
		initChunkWithNoGlitterDiscovered();
		if (done) return;
		initGlitterDetected();
		if (done) return;
		initOnlyOneChunkWithGoldRemaining();
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
		if (action == Action.GRAB) {
			invokeGoldPickedUp();
		}
	}


	private void onChunkExplored(Chunk chunk) {
		invokeGlitterDetected(chunk);
		invokeGoldLocated(chunk);
		invokeChunkWithNoGlitterDiscovered(chunk);
	}


	private void onPerceptChanged(Chunk chunk, PerceptChanged perceptChanged) {
		if (done) return;

		if (perceptChanged.getPercept() == GOLD) {
			invokeGoldLocated(chunk);
			invokeOnlyOneChunkWithGoldRemaining();
		}
	}




	private void initGoldPickedUp() {
		this.hasGold = false;
		invokeGoldPickedUp();
	}


	private void initGoldLocated() {
		for (Chunk chunk : worldModel.getChunks()) {
			if (chunk.getPercepts().getGold() == TRUE) {
				onGoldLocated(chunk);
				break;
			}
		}
	}


	private void initChunkWithNoGlitterDiscovered() {
		Arrays.stream(worldModel.getChunks())
				.forEach(this::invokeChunkWithNoGlitterDiscovered);
	}


	private void initGlitterDetected() {
		for (Chunk chunk : worldModel.getChunks()) {
			if (chunk.getPercepts().getGlitter() == TRUE) {
				onGlitterDetected(chunk);
				break;
			}
		}
	}


	private void initOnlyOneChunkWithGoldRemaining() {
		invokeOnlyOneChunkWithGoldRemaining();
	}




	private void invokeGoldPickedUp() {
		boolean hasGold = worldModel.hasGold();
		if (hasGold != this.hasGold) {
			if (this.hasGold) throw new IllegalStateException("Gold added in mid-game.");
			this.hasGold = false;
			onGoldPickedUp();
		}
	}


	private void invokeGoldLocated(Chunk chunk) {
		if (chunk.getPercepts().getGold() == TRUE)
			onGoldLocated(chunk);
	}


	private void invokeChunkWithNoGlitterDiscovered(Chunk chunk) {
		if (chunk.getPercepts().getGlitter() == FALSE)
			onChunkWithNoGlitterDiscovered(chunk);
	}


	private void invokeGlitterDetected(Chunk chunk) {
		if (chunk.getPercepts().getGlitter() == TRUE)
			onGlitterDetected(chunk);
	}


	private void invokeOnlyOneChunkWithGoldRemaining() {
		Chunk onlyWithGold = null;
		for (Chunk chunk : worldModel.getChunks()) {
			if (chunk.getPercepts().getGold().isSatisfiable()) {
				if (onlyWithGold == null)
					onlyWithGold = chunk;
				else return;
			}
		}

		if (onlyWithGold != null)
			onOnlyOneChunkWithGoldRemaining(onlyWithGold);
	}




	private void onGoldPickedUp() {
		onPreDone();
		Arrays.stream(worldModel.getChunks())
				.forEach(chunk -> chunk.getPercepts().setGold(FALSE));
		this.goldLocated = true;
		onDone();
	}


	private void onGoldLocated(Chunk chunk) {
		Arrays.stream(worldModel.getChunks())
				.filter(c -> !c.equals(chunk))
				.forEach(c -> c.getPercepts().setGold(FALSE));
		this.goldLocated = true;
	}


	private void onChunkWithNoGlitterDiscovered(Chunk chunk) {
		chunk.getPercepts().setGold(FALSE);
	}


	private void onGlitterDetected(Chunk chunk) {
		chunk.getPercepts().setGold();
	}


	private void onOnlyOneChunkWithGoldRemaining(Chunk chunk) {
		chunk.getPercepts().setGold();
	}


}
