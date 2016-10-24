package wumpusworld.aiclient.model;


import wumpusworld.World;
import wumpusworld.aiclient.Percept;
import wumpusworld.aiclient.util.Event;
import wumpusworld.aiclient.util.EventInterface;

import java.util.Objects;

import static wumpusworld.aiclient.Percept.*;
import static wumpusworld.aiclient.model.TFUValue.*;




/**
 * A collection if percepts.
 * Created by Nejc on 12. 10. 2016.
 */
public class PerceptCollection implements Cloneable {




	private static final int BREEZE_FALSE_BIT   = 1;
	private static final int BREEZE_TRUE_BIT    = 1 << 1;
	private static final int PIT_FALSE_BIT      = 1 << 2;
	private static final int PIT_TRUE_BIT       = 1 << 3;
	private static final int STENCH_FALSE_BIT   = 1 << 4;
	private static final int STENCH_TRUE_BIT    = 1 << 5;
	private static final int WUMPUS_FALSE_BIT   = 1 << 6;
	private static final int WUMPUS_TRUE_BIT    = 1 << 7;
	private static final int GLITTER_FALSE_BIT  = 1 << 8;
	private static final int GLITTER_TRUE_BIT   = 1 << 9;
	private static final int GOLD_FALSE_BIT     = 1 << 10;
	private static final int GOLD_TRUE_BIT      = 1 << 11;


	private int value;
	private final Event<PerceptChanged> perceptChangedEvent = new Event<>();




	public int getValue() {
		return value;
	}


	public EventInterface<PerceptChanged> getPerceptChangedEvent() {
		return perceptChangedEvent.getInterface();
	}




	public PerceptCollection() {
		this.value = 0;
	}


	private PerceptCollection(int value) {
		this.value = value;
	}




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


	public TFUValue getPercept(Percept percept) {
		Objects.requireNonNull(percept);
		int falseBitCode = getPerceptFalseBitCode(percept);
		return getPercept(falseBitCode, falseBitCode << 1);
	}


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




	public TFUValue getSafe() {
		return getWumpus()
				.negate()
				.and(getPit().negate());
	}


	public boolean isSafe() {
		return getWumpus() == FALSE
				&& getPit() == FALSE;
	}



	public void copyFrom(PerceptCollection other) {
		Objects.requireNonNull(other);

		this.setBreeze(other.getBreeze());
		this.setPit(other.getPit());
		this.setStench(other.getStench());
		this.setWumpus(other.getWumpus());
		this.setGlitter(other.getGlitter());
		this.setGold(other.getGold());
	}


	@Override
	protected Object clone() {
		return new PerceptCollection(this.value);
	}




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
