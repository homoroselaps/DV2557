package wumpusworld.aiclient.assumptionmaking;


import wumpusworld.aiclient.model.Chunk;
import wumpusworld.aiclient.model.WorldModel;
import wumpusworld.aiclient.util.EventHandler;

import java.security.KeyStore;
import java.util.Arrays;
import java.util.Objects;


import static wumpusworld.aiclient.model.TFUValue.*;




/**
 * Created by Nejc on 17. 10. 2016.
 */
public class GoldAssumptionMaker implements AssumptionMaker {




	private final WorldModel worldModel;
	private boolean done;
	private EventHandler<Chunk> chunkExploredEventListener;




	public WorldModel getWorldModel() {
		return worldModel;
	}


	@Override
	public boolean isDone() {
		return done;
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
		subscribe();

		initGlitterDetected();
	}


	@Override
	public void dispose() {
		unsubscribe();
	}


	private void subscribe() {
		if (chunkExploredEventListener == null) {
			chunkExploredEventListener = ((sender, arg) -> onChunkExplored(arg));
			worldModel.getChunkExploredEvent().subscribe(chunkExploredEventListener);
		}
	}


	private void unsubscribe() {
		if (chunkExploredEventListener != null)
			worldModel.getChunkExploredEvent().unsubscribe(chunkExploredEventListener);
		chunkExploredEventListener = null;
	}


	private void onDone() {
		unsubscribe();
		done = true;
	}




	private void onChunkExplored(Chunk chunk) {
		invokeGlitterDetected(chunk);
	}




	private void initGlitterDetected() {
		Arrays.stream(worldModel.getChunks())
				.filter(chunk -> chunk.getPercepts().getGlitter() == TRUE)
				.findFirst()
				.ifPresent(this::onGlitterDetected);
	}


	private void invokeGlitterDetected(Chunk chunk) {
		if (chunk.getPercepts().getGlitter() == TRUE)
			onGlitterDetected(chunk);
	}


	private void onGlitterDetected(Chunk chunk) {
		Arrays.stream(worldModel.getChunks())
				.forEach(c -> c.getPercepts().setGold(c.equals(chunk) ? TRUE : FALSE));
		onDone();
	}


}
