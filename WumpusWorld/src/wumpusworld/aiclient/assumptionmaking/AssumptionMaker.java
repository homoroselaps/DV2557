package wumpusworld.aiclient.assumptionmaking;


import wumpusworld.aiclient.util.Disposable;




/**
 * Represents an assumption-making class.
 * <p>
 * Created by Nejc on 13. 10. 2016.
 */
public interface AssumptionMaker extends Disposable {


    /**
     * Checks if the AssumptionMaker has finished its work.
     *
     * @return Whether or not all operations are finished.
     */
    boolean isDone();


    /**
     * Initializes the AssumptionMaker.
     */
    void init();


    /**
     * Forces the AssumptionMaker to update its state. This method should be called if the associated object has changed and AssumptionMaker was not able to detect that change.
     */
    void update();


}