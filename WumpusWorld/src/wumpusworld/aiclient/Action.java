package wumpusworld.aiclient;


import wumpusworld.World;




/**
 * An enum of actions. A more convenient representation than the one used by {@link wumpusworld.World}.
 * Created by Nejc on 12. 10. 2016.
 */
public enum Action {


    MOVE        (World.A_MOVE),
    GRAB        (World.A_GRAB),
    CLIMB       (World.A_CLIMB),
    SHOOT       (World.A_SHOOT),
    TURN_LEFT   (World.A_TURN_LEFT),
    TURN_RIGHT  (World.A_TURN_RIGHT);


    private final String legacyAction;


    public String getLegacyAction() {
        return legacyAction;
    }


    Action(String legacyAction) {
        this.legacyAction = legacyAction;
    }


    public static Action fromLegacyAction(String legacyAction) {
        for (Action action : Action.values()) {
            if (action.legacyAction.equals(legacyAction))
                return action;
        }
        throw new IllegalArgumentException();
    }


}
