package wumpusworld.aiclient;


import wumpusworld.World;




/**
 * An enum of actions. A more convenient representation than the one used by {@link wumpusworld.World}.
 * <p>
 * Created by Nejc on 12. 10. 2016.
 */
public enum Action {


    /**
     * Instruct the player to move forward.
     */
    MOVE(World.A_MOVE),
    /**
     * Instruct the player to grab gold.
     */
    GRAB(World.A_GRAB),
    /**
     * Instructs the player to climb from a pit.
     */
    CLIMB(World.A_CLIMB),
    /**
     * Instructs the player to shoot the arrow.
     */
    SHOOT(World.A_SHOOT),
    /**
     * Instructs the player to turn left.
     */
    TURN_LEFT(World.A_TURN_LEFT),
    /**
     * Instructs the player to turn right.
     */
    TURN_RIGHT(World.A_TURN_RIGHT);


    private final String legacyAction;


    /**
     * Gets the string representation of the action used by {@link World}.
     *
     * @return The string representation used by {@link World}.
     */
    public String getLegacyAction() {
        return legacyAction;
    }


    Action(String legacyAction) {
        this.legacyAction = legacyAction;
    }


    /**
     * Converts an action string used by {@link World} to an {@link Action} enum value.
     *
     * @param legacyAction The action string.
     * @return The {@link Action} enum value.
     */
    public static Action fromLegacyAction(String legacyAction) {
        for (Action action : Action.values()) {
            if (action.legacyAction.equals(legacyAction))
                return action;
        }
        throw new IllegalArgumentException();
    }


}
