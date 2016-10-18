package wumpusworld;


import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import wumpusworld.aiclient.assumptionmaking.AssumptionManager;
import wumpusworld.aiclient.model.WorldModel;




/**
 * Contains starting code for creating your own Wumpus World agent.
 * Currently the agent only make a random decision each turn.
 *
 * @author Johan Hagelbäck
 */
public class MyAgent implements Agent {


	private final WorldModel worldModel;
	private final AssumptionManager assumptionManager;


	/**
	 * Creates a new instance of your solver agent.
	 *
	 * @param world Current world state
	 */
	public MyAgent(World world) {
		this.worldModel = new WorldModel(world);
		this.assumptionManager = new AssumptionManager(this.worldModel);
		this.assumptionManager.initAll();
	}


	/**
	 * Asks your solver agent to execute an action.
	 */
	public void doAction() {
		throw new NotImplementedException();
	}


}

