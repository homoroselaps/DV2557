package wumpusworld.aiclient.assumptionmaking;


import wumpusworld.aiclient.model.Chunk;
import wumpusworld.aiclient.model.PerceptChanged;
import wumpusworld.aiclient.model.WorldModel;
import wumpusworld.aiclient.util.EventHandler;
import wumpusworld.aiclient.util.EventInterface;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import static wumpusworld.aiclient.Percept.PIT;
import static wumpusworld.aiclient.model.TFUValue.FALSE;
import static wumpusworld.aiclient.model.TFUValue.TRUE;
import static wumpusworld.aiclient.model.TFUValue.UNKNOWN;




/**
 * Makes assumptions on where pits may be located.
 * Created by Nejc on 17. 10. 2016.
 */
public class PitAssumptionMaker implements AssumptionMaker {




	private final WorldModel worldModel;
	private HashMap<EventHandler<?>, EventInterface<?>> listeners;
	private boolean done;




	public WorldModel getWorldModel() {
		return worldModel;
	}


	public boolean isDone() {
		return done;
	}




	public PitAssumptionMaker(WorldModel worldModel) {
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

		initChunkWithNoBreezeDiscovered();
		if (done) return;
		initChunkWithBreezeHasONlyOneAdjacentChunkWitPit();
	}


	@Override
	public void dispose() {
		unsubscribe();
	}


	private void subscribe() {
		if (listeners != null)
			unsubscribe();

		listeners = new HashMap<>(1);

		EventHandler<Chunk> chunkExploredEventHandler = (sender, arg) -> onChunkExplored(arg);
		worldModel.getChunkExploredEvent().subscribe(chunkExploredEventHandler);
		listeners.put(chunkExploredEventHandler, worldModel.getActionEvent());
	}


	private void unsubscribe() {
		if (listeners != null)
			listeners.entrySet().forEach(pair -> pair.getValue().unsubscribe(pair.getKey()));
		listeners = null; // let GC do its work
	}


	private void checkDone() {
		done = Arrays.stream(worldModel.getChunks())
				.allMatch(chunk -> chunk.getPercepts().getPit() != UNKNOWN);
	}




	private void onChunkExplored(Chunk chunk) {
		invokeChunkWithNoBreezeDiscovered(chunk);
		invokeChunkWithBreezeHasOnlyOneAdjacentChunkWithPit(chunk, true);
	}




	private void initChunkWithNoBreezeDiscovered() {
		Arrays.stream(worldModel.getChunks())
				.filter(chunk -> chunk.getPercepts().getBreeze() == FALSE)
				.forEach(this::invokeChunkWithNoBreezeDiscovered);
	}


	private void initChunkWithBreezeHasONlyOneAdjacentChunkWitPit() {
		Arrays.stream(worldModel.getChunks())
				.filter(chunk -> chunk.getPercepts().getBreeze() == TRUE)
				.forEach(chunk -> invokeChunkWithBreezeHasOnlyOneAdjacentChunkWithPit(chunk, true));
	}




	private void invokeChunkWithNoBreezeDiscovered(Chunk chunk) {
		if (chunk.getPercepts().getBreeze() == FALSE)
			onChunkWithNoBreezeDiscovered(chunk);
	}


	private void invokeChunkWithBreezeHasOnlyOneAdjacentChunkWithPit(Chunk chunk, boolean subscribe) {
		if (chunk.getPercepts().getBreeze() != TRUE) return;

		Optional<Chunk> theOnlyOne = chunk.getOnlyAdjacentChunkWithSatisfiablePercept(PIT);
		if (theOnlyOne.isPresent()) {
			onChunkWithBreezeHasOnlyOneAdjacentChunkWithPit(theOnlyOne.get());
		} else if (subscribe) {
			Arrays.stream(chunk.getAdjacent())
					.filter(c -> c.getPercepts().getBreeze().isSatisfiable())
					.forEach(c -> {
						EventHandler<PerceptChanged> perceptChangedEventHandler = (sender, arg) -> {
							if (arg.getPercept() == PIT && arg.getNewValue() == FALSE)
								invokeChunkWithBreezeHasOnlyOneAdjacentChunkWithPit(chunk, false);
						};
						c.getPercepts().getPerceptChangedEvent().subscribe(perceptChangedEventHandler);
						listeners.put(perceptChangedEventHandler, c.getPercepts().getPerceptChangedEvent());
					});
		}
	}




	private void onChunkWithNoBreezeDiscovered(Chunk chunk) {
		Arrays.stream(chunk.getAdjacent())
				.forEach(c -> c.getPercepts().setPit(FALSE));
		checkDone();
	}


	private void onChunkWithBreezeHasOnlyOneAdjacentChunkWithPit(Chunk adjacentChunk) {
		adjacentChunk.getPercepts().setPit();
		checkDone();
	}


}
