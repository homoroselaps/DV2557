package wumpusworld.aiclient;


/**
 * Represents a direction the player is facing.
 * Created by Nejc on 13. 10. 2016.
 */
public enum Direction {


	UP,
	RIGHT,
	DOWN,
	LEFT;


	public static Direction fromLegacyDirection(int direction) {
		return Direction.values()[direction];
	}


	public int toLegacyDirection() {
		return this.ordinal();
	}


}
