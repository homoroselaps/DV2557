package wumpusworld.aiclient.assumptionmaking;


import wumpusworld.aiclient.model.WorldModel;

import java.util.Objects;




/**
 * A wrapper that hold together {@link WumpusAssumptionMaker}, {@link PitAssumptionMaker} and {@link GoldAssumptionMaker}.
 * <p>
 * Created by Nejc on 13. 10. 2016.
 */
public class AssumptionManager implements AssumptionMaker {




    private final WorldModel worldModel;
    private final WumpusAssumptionMaker wumpusAssumptionMaker;
    private final PitAssumptionMaker pitAssumptionMaker;
    private final GoldAssumptionMaker goldAssumptionMaker;




    public WorldModel getWorldModel() {
        return worldModel;
    }


    public WumpusAssumptionMaker getWumpusAssumptionMaker() {
        return wumpusAssumptionMaker;
    }


    public PitAssumptionMaker getPitAssumptionMaker() {
        return pitAssumptionMaker;
    }


    public GoldAssumptionMaker getGoldAssumptionMaker() {
        return goldAssumptionMaker;
    }




    public AssumptionManager(WorldModel worldModel) {
        Objects.requireNonNull(worldModel);

        this.worldModel = worldModel;
        this.wumpusAssumptionMaker = new WumpusAssumptionMaker(worldModel);
        this.pitAssumptionMaker = new PitAssumptionMaker(worldModel);
        this.goldAssumptionMaker = new GoldAssumptionMaker(worldModel);
    }




    /**
     * Checks if all underlying assumption makers are done.
     *
     * @return
     */
    public boolean isDone() {
        return wumpusAssumptionMaker.isDone()
                && pitAssumptionMaker.isDone()
                && goldAssumptionMaker.isDone();
    }


    /**
     * Initializes all underlying assumption makers.
     */
    public void init() {
        wumpusAssumptionMaker.init();
        pitAssumptionMaker.init();
        goldAssumptionMaker.init();
    }


    /**
     * Updates all underlying assumption makers.
     */
    public void update() {
        wumpusAssumptionMaker.update();
        pitAssumptionMaker.update();
        goldAssumptionMaker.update();
    }




    /**
     * Disposes all underlying assumption makers.
     */
    @Override
    public void dispose() {
        wumpusAssumptionMaker.dispose();
        pitAssumptionMaker.dispose();
        goldAssumptionMaker.dispose();
    }


}
