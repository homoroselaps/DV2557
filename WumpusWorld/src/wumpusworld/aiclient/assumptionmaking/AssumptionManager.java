package wumpusworld.aiclient.assumptionmaking;


import wumpusworld.aiclient.model.WorldModel;
import wumpusworld.aiclient.util.Disposable;

import java.util.Objects;




/**
 * Makes assumptions on a {@link WorldModel}.
 * Created by Nejc on 13. 10. 2016.
 */
public class AssumptionManager implements Disposable {




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




    public boolean allDone() {
        return wumpusAssumptionMaker.isDone()
                && pitAssumptionMaker.isDone()
                && goldAssumptionMaker.isDone();
    }


    public void initAll() {
        wumpusAssumptionMaker.init();
        pitAssumptionMaker.init();
        goldAssumptionMaker.init();
    }


    public void updateAll() {
        wumpusAssumptionMaker.updateAll();
        pitAssumptionMaker.updateAll();
        goldAssumptionMaker.updateAll();
    }




    @Override
    public void dispose() {
        wumpusAssumptionMaker.dispose();
        pitAssumptionMaker.dispose();
        goldAssumptionMaker.dispose();
    }


}
