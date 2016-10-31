package wumpusworld.aiclient.assumptionmaking;


import wumpusworld.aiclient.util.Disposable;




public interface AssumptionMaker extends Disposable {


    boolean isDone();


    void init();


    void updateAll();


}