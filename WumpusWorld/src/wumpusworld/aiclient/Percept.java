package wumpusworld.aiclient;


import wumpusworld.World;




/**
 * An enum of percepts. A more convenient representation than the one used by {@link wumpusworld.World}.
 * Created by Nejc on 12. 10. 2016.
 */
public enum Percept {


	BREEZE(World.BREEZE),
	PIT(World.PIT),
	STENCH(World.STENCH),
	WUMPUS(World.WUMPUS),
	GLITTER(World.GLITTER),
	GOLD(World.GOLD);


	private final String legacyPercept;


	public String getLegacyPercept() {
		return legacyPercept;
	}


	Percept(String legacyPercept) {
		this.legacyPercept = legacyPercept;
	}


	public static Percept fromLegacyPercept(String legacyPercept) {
		for (Percept percept : Percept.values()) {
			if (percept.legacyPercept.equals(legacyPercept))
				return percept;
		}
		throw new IllegalArgumentException();
	}


}
