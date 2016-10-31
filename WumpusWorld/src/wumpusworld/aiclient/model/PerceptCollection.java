package wumpusworld.aiclient.model;


import wumpusworld.World;
import wumpusworld.aiclient.Percept;
import wumpusworld.aiclient.util.Event;
import wumpusworld.aiclient.util.EventInterface;

import java.util.Objects;

import static wumpusworld.aiclient.Percept.*;
import static wumpusworld.aiclient.model.TFUValue.*;




/**
 * A collection of {@link Percept}s' values.
 * Created by Nejc on 12. 10. 2016.
 */
public class PerceptCollection implements Cloneable {




    private static final int BREEZE_FALSE_BIT = 1;
    private static final int BREEZE_TRUE_BIT = 1 << 1;
    private static final int PIT_FALSE_BIT = 1 << 2;
    private static final int PIT_TRUE_BIT = 1 << 3;
    private static final int STENCH_FALSE_BIT = 1 << 4;
    private static final int STENCH_TRUE_BIT = 1 << 5;
    private static final int WUMPUS_FALSE_BIT = 1 << 6;
    private static final int WUMPUS_TRUE_BIT = 1 << 7;
    private static final int GLITTER_FALSE_BIT = 1 << 8;
    private static final int GLITTER_TRUE_BIT = 1 << 9;
    private static final int GOLD_FALSE_BIT = 1 << 10;
    private static final int GOLD_TRUE_BIT = 1 << 11;


    private int value;
    private final Event<PerceptChanged> perceptChangedEvent = new Event<>();




    /**
     * Gets the value that represents percept's states.
     *
     * @return The value.
     */
    public int getValue() {
        return value;
    }


    /**
     * Gets the event that is called whenever a percept's value changes.
     *
     * @return
     */
    public EventInterface<PerceptChanged> getPerceptChangedEvent() {
        return perceptChangedEvent.getInterface();
    }




    /**
     * Creates a new instance of {@link PerceptCollection} with all percepts set to {@link TFUValue#UNKNOWN}.
     */
    public PerceptCollection() {
        this.value = 0;
    }


    /**
     * Creates a new instance of {@link PerceptCollection} with percepts' values initialized from given value.
     *
     * @param value Value representing states of percepts.
     */
    private PerceptCollection(int value) {
        this.value = value;
    }




    /**
     * Creates a new instance of {@link PerceptCollection} from given {@link World}'s chunk.
     *
     * @param world Associated world.
     * @param x     Chunk's X coordinate.
     * @param y     Chunk's Y coordinate.
     * @return A new instane of {@link PerceptCollection}.
     */
    public static PerceptCollection fromWorld(World world, int x, int y) {
        Objects.requireNonNull(world);

        PerceptCollection perceptCollection = new PerceptCollection();

        if (world.isVisited(x, y)) {
            perceptCollection.setBreeze(fromValue(world.hasBreeze(x, y)));
            perceptCollection.setPit(fromValue(world.hasPit(x, y)));
            perceptCollection.setStench(fromValue(world.hasStench(x, y)));
            perceptCollection.setWumpus(fromValue(world.hasWumpus(x, y)));
            perceptCollection.setGlitter(fromValue(world.hasGlitter(x, y)));
//			perceptCollection.setGold(fromValue(world.hasGlitter(x, y)));
        } // otherwise everything is UNKNOWN

        return perceptCollection;
    }




    private static int getPerceptFalseBitCode(Percept percept) {
        return 1 << (percept.ordinal() * 2);
    }


    private static Percept getPerceptFromFalseBitCode(int perceptFalseBitCode) {
        if (perceptFalseBitCode == 0)
            throw new IllegalArgumentException();

        int i = 0;
        for (; i < 12; i++) {
            if (perceptFalseBitCode == 1)
                break;
            perceptFalseBitCode >>= 1;
        }

        int ordinal = i / 2;

        Percept[] values = Percept.values();
        if (ordinal >= values.length)
            throw new IllegalArgumentException();

        return values[ordinal];
    }




    private static TFUValue getPercept(int value, int falseBit, int trueBit) {
        if ((value & trueBit) == trueBit)
            return TRUE;
        if ((value & falseBit) == falseBit)
            return FALSE;
        return UNKNOWN;
    }


    private static int setPercept(int value, int falseBit, int trueBit, TFUValue newValue) {
        Objects.requireNonNull(value);

        switch (newValue) {
            case FALSE:
                value |= falseBit;
                value &= ~trueBit;
                break;
            case TRUE:
                value |= trueBit;
                value &= ~falseBit;
                break;
            case UNKNOWN:
                value &= ~(falseBit | trueBit);
                break;
        }

        return value;
    }


    private TFUValue getPercept(int falseBit, int trueBit) {
        return getPercept(this.value, falseBit, trueBit);
    }


    private boolean setPercept(int falseBit, int trueBit, TFUValue value, Percept percept) {
        int oldValue = this.value;

        this.value = setPercept(this.value, falseBit, trueBit, value);

        boolean changed = this.value != oldValue;
        if (changed)
            perceptChangedEvent.invoke(this, new PerceptChanged(percept, getPercept(this.value, falseBit, trueBit), getPercept(oldValue, falseBit, trueBit)));

        return changed;
    }


    /**
     * Gets percept's value.
     *
     * @param percept The percept.
     * @return Given percept's value.
     */
    public TFUValue getPercept(Percept percept) {
        Objects.requireNonNull(percept);
        int falseBitCode = getPerceptFalseBitCode(percept);
        return getPercept(falseBitCode, falseBitCode << 1);
    }


    /**
     * Sets percept's value.
     *
     * @param percept The percept.
     * @param value   The value for the percept.
     * @return Whether or not the percept's value has changed.
     */
    public boolean setPercept(Percept percept, TFUValue value) {
        Objects.requireNonNull(percept);
        int falseBitCode = getPerceptFalseBitCode(percept);
        return setPercept(falseBitCode, falseBitCode << 1, value, percept);
    }




    public TFUValue getBreeze() {
        return getPercept(BREEZE_FALSE_BIT, BREEZE_TRUE_BIT);
    }


    public TFUValue getPit() {
        return getPercept(PIT_FALSE_BIT, PIT_TRUE_BIT);
    }


    public TFUValue getStench() {
        return getPercept(STENCH_FALSE_BIT, STENCH_TRUE_BIT);
    }


    public TFUValue getWumpus() {
        return getPercept(WUMPUS_FALSE_BIT, WUMPUS_TRUE_BIT);
    }


    public TFUValue getGlitter() {
        return getPercept(GLITTER_FALSE_BIT, GLITTER_TRUE_BIT);
    }


    public TFUValue getGold() {
        return getPercept(GOLD_FALSE_BIT, GOLD_TRUE_BIT);
    }


    public boolean setBreeze(TFUValue value) {
        return setPercept(BREEZE_FALSE_BIT, BREEZE_TRUE_BIT, value, BREEZE);
    }


    public boolean setPit(TFUValue value) {
        return setPercept(PIT_FALSE_BIT, PIT_TRUE_BIT, value, PIT);
    }


    public boolean setStench(TFUValue value) {
        return setPercept(STENCH_FALSE_BIT, STENCH_TRUE_BIT, value, STENCH);
    }


    public boolean setWumpus(TFUValue value) {
        return setPercept(WUMPUS_FALSE_BIT, WUMPUS_TRUE_BIT, value, WUMPUS);
    }


    public boolean setGlitter(TFUValue value) {
        return setPercept(GLITTER_FALSE_BIT, GLITTER_TRUE_BIT, value, GLITTER);
    }


    public boolean setGold(TFUValue value) {
        return setPercept(GOLD_FALSE_BIT, GOLD_TRUE_BIT, value, GOLD);
    }


    public boolean setBreeze() {
        return setPercept(BREEZE_FALSE_BIT, BREEZE_TRUE_BIT, TRUE, BREEZE);
    }


    public boolean setPit() {
        return setPercept(PIT_FALSE_BIT, PIT_TRUE_BIT, TRUE, PIT);
    }


    public boolean setStench() {
        return setPercept(STENCH_FALSE_BIT, STENCH_TRUE_BIT, TRUE, STENCH);
    }


    public boolean setWumpus() {
        return setPercept(WUMPUS_FALSE_BIT, WUMPUS_TRUE_BIT, TRUE, WUMPUS);
    }


    public boolean setGlitter() {
        return setPercept(GLITTER_FALSE_BIT, GLITTER_TRUE_BIT, TRUE, GLITTER);
    }


    public boolean setGold() {
        return setPercept(GOLD_FALSE_BIT, GOLD_TRUE_BIT, TRUE, GOLD);
    }




    /**
     * Gets a {@link TFUValue} representing safeness deduced from percepts {@link Percept#WUMPUS} and {@link Percept#PIT}.
     *
     * @return A conjunction of percepts {@link Percept#WUMPUS} and {@link Percept#PIT}.
     */
    public TFUValue getSafe() {
        return getWumpus()
                .negate()
                .and(getPit().negate());
    }


    /**
     * Checks if this percept collection does not contain Wumpus or pit.
     *
     * @return {@code true} if this percept collection does not contains {@link Percept#WUMPUS} or {@link Percept#PIT}, {@code false} otherwise.
     */
    public boolean isSafe() {
        return getWumpus() == FALSE
                && getPit() == FALSE;
    }


    /**
     * Copies percepts from another percept collection.
     *
     * @param other The other percept collectin to copy from.
     */
    public void copyFrom(PerceptCollection other) {
        Objects.requireNonNull(other);

        this.setBreeze(other.getBreeze());
        this.setPit(other.getPit());
        this.setStench(other.getStench());
        this.setWumpus(other.getWumpus());
        this.setGlitter(other.getGlitter());
        this.setGold(other.getGold());
    }


    /**
     * Clones current instance.
     *
     * @return The clone of current instance.
     */
    @Override
    protected Object clone() {
        return new PerceptCollection(this.value);
    }




    /**
     * Gets the string representation of the current instance.
     *
     * @return String representation of the current instance.
     */
    @Override
    public String toString() {
        String res = "{ ";

        for (Percept percept : Percept.values()) {
            if (res.length() != 2)
                res += ", ";
            res += ", "
                    + percept.getLegacyPercept()
                    + ": "
                    + getPercept(percept).toString();
        }

        return res + " }";
    }


}
