package wumpusworld.aiclient.assumptionmaking;


import wumpusworld.aiclient.model.WorldModel;
import wumpusworld.aiclient.util.Disposable;

import java.util.Arrays;
import java.util.Objects;




/**
 * Makes assumptions on a {@link WorldModel}.
 * Created by Nejc on 13. 10. 2016.
 */
public class AssumptionManager implements AssumptionMaker {




	private final WorldModel worldModel;
	private AssumptionMaker[] assumptionMakers;
	private boolean initialized;




	public WorldModel getWorldModel() {
		return worldModel;
	}


	public boolean isInitialized() {
		return initialized;
	}


	@Override
	public boolean isDone() {
		return Arrays.stream(assumptionMakers)
				.allMatch(AssumptionMaker::isDone);
	}




	public AssumptionManager(WorldModel worldModel, AssumptionMaker[] assumptionMakers) {
		Objects.requireNonNull(worldModel);
		Objects.requireNonNull(assumptionMakers);

		for (AssumptionMaker assumptionMaker : assumptionMakers) {
			if (assumptionMaker == null)
				throw new IllegalArgumentException();
		}

		this.worldModel = worldModel;
		this.assumptionMakers = assumptionMakers.clone();
	}




	public static AssumptionManager getDefault(WorldModel worldModel) {
		Objects.requireNonNull(worldModel);

		return new AssumptionManager(worldModel, new AssumptionMaker[]{
				new WumpusAssumptionMaker(worldModel),
				new PitAssumptionMaker(worldModel),
				new GoldAssumptionMaker(worldModel)
		});
	}




	@Override
	public void init() {
		if (initialized)
			throw new IllegalStateException("Already initialized.");
		initialized = true;

		Arrays.stream(assumptionMakers)
				.forEach(AssumptionMaker::init);
	}


	@Override
	public void updateAll() {
		Arrays.stream(assumptionMakers)
				.forEach(AssumptionMaker::updateAll);
	}


	@Override
	public void dispose() {
		Arrays.stream(assumptionMakers)
				.forEach(Disposable::dispose);
	}


}
