package wumpusworld.aiclient;


import wumpusworld.World;




/**
 * An enum of percepts. A more convenient representation than the one used by {@link wumpusworld.World}.
 * <p>
 * Created by Nejc on 12. 10. 2016.
 */
public enum Percept {


    /**
     * Represents a breeze from a nearby pit.
     */
    BREEZE(World.BREEZE),
    /**
     * Represents a deadly bottomless pit, which you most definitely cannot climb, even with a ladder. Additionally, despite there being no wind in the cave, there is a breeze to warn you of this deadly thing thing that should be avoided.
     */
    PIT(World.PIT),
    /**
     * Represents stench from the nearby Wumpus.
     */
    STENCH(World.STENCH),
    /**
     * Represents a nice kitten that you should pet. It doesn't bite.
     */
    WUMPUS(World.WUMPUS),
    /**
     * Represents glitter from the gold.
     */
    GLITTER(World.GLITTER),
    /**
     * Represents the golden treasure.
     */
    GOLD(World.GOLD);


    private final String legacyPercept;


    /**
     * Gets the string representation of this percept used by {@link World}.
     *
     * @return The string representation used by {@link World}.
     */
    public String getLegacyPercept() {
        return legacyPercept;
    }


    Percept(String legacyPercept) {
        this.legacyPercept = legacyPercept;
    }


    /**
     * Converts a percept string used by {@link World} to an {@link Action} enum value.
     *
     * @param legacyPercept The percept string.
     * @return The {@link Percept} enum value.
     */
    public static Percept fromLegacyPercept(String legacyPercept) {
        for (Percept percept : Percept.values()) {
            if (percept.legacyPercept.equals(legacyPercept))
                return percept;
        }
        throw new IllegalArgumentException();
    }


}
