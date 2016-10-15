package wumpusworld.qlearning;

import wumpusworld.World;

/**
 * Wrapper class for the actions an agent can perform.
 * String constants are ugly
 *
 * Created by smarti on 14.10.16.
 */
public enum Action {
    turnLeft, turnRight, walk, shoot, climb, grabGold;

    public World makeAction(World world){
        World result = world.cloneWorld();
        result.doAction(getCommandName());
        return result;
    }

    public String getCommandName() {
        String result = null;
        switch (this){
            case turnLeft:
                result = World.A_TURN_LEFT;
                break;
            case turnRight:
                result = World.A_TURN_RIGHT;
                break;
            case walk:
                result = World.A_MOVE;
                break;
            case shoot:
                result = World.A_SHOOT;
                break;
            case climb:
                result = World.A_CLIMB;
                break;
            case grabGold:
                result = World.A_GRAB;
                break;
        }
        return result;
    }
}
