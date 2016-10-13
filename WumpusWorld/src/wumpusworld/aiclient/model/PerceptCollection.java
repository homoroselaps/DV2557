package wumpusworld.aiclient.model;


import wumpusworld.World;
import wumpusworld.aiclient.Percept;
import wumpusworld.aiclient.util.Event;
import wumpusworld.aiclient.util.EventInterface;

import java.util.Objects;

import static wumpusworld.aiclient.model.TFUValue.*;




/**
 * A collection if percepts.
 * Created by Nejc on 12. 10. 2016.
 */
public class PerceptCollection implements Cloneable {




	private static final int BREEZE_FALSE_BIT = 1 << 1;
	private static final int BREEZE_TRUE_BIT = 1 << 2;
	private static final int PIT_FALSE_BIT = 1 << 3;
	private static final int PIT_TRUE_BIT = 1 << 4;
	private static final int STENCH_FALSE_BIT = 1 << 5;
	private static final int STENCH_TRUE_BIT = 1 << 6;
	private static final int WUMPUS_FALSE_BIT = 1 << 7;
	private static final int WUMPUS_TRUE_BIT = 1 << 8;
	private static final int GLITTER_FALSE_BIT = 1 << 9;
	private static final int GLITTER_TRUE_BIT = 1 << 10;
	private static final int GOLD_FALSE_BIT = 1 << 11;
	private static final int GOLD_TRUE_BIT = 1 << 12;


	private int value;




	public int getValue() {
		return value;
	}




	public PerceptCollection() {
		value = 0;
	}


	private PerceptCollection(int value) {
		this.value = value;
	}




	public static PerceptCollection fromWorld(World world, int x, int y) {
		Objects.requireNonNull(world);

		PerceptCollection perceptCollection = new PerceptCollection();

		if (world.hasBreeze(x, y))
			perceptCollection.setBreeze();
		if (world.hasPit(x, y))
			perceptCollection.setPit();
		if (world.hasStench(x, y))
			perceptCollection.setStench();
		if (world.hasWumpus(x, y))
			perceptCollection.setWumpus();
		if (world.hasGlitter(x, y))
			perceptCollection.setGlitter();
		if (world.hasGold())
			perceptCollection.setGold();

		return perceptCollection;
	}





	private static int getPerceptFalseBitCode(Percept percept) {
		return (percept.ordinal() * 2) + 1;
	}


	private static Percept getPerceptFromFalseBitCode(int perceptFalseBitCode) {
		int ordinal = (perceptFalseBitCode - 1) / 2;
		return Percept.values()[ordinal];
	}




	private TFUValue getPercept(int falseBit, int trueBit) {
		if ((value & trueBit) == trueBit)
			return TRUE;
		if ((value & falseBit) == falseBit)
			return FALSE;
		return UNKNOWN;
	}


	private boolean setPercept(int falseBit, int trueBit, TFUValue value) {
		Objects.requireNonNull(value);

		int old = this.value;
		switch (value) {
			case FALSE:
				this.value |= falseBit;
				this.value &= ~trueBit;
			case TRUE:
				this.value |= trueBit;
				this.value &= ~falseBit;
			case UNKNOWN:
				this.value &= ~(falseBit | trueBit);
		}

		return this.value != old;
	}


	public TFUValue getPercept(Percept percept) {
		Objects.requireNonNull(percept);
		int falseBitCode = getPerceptFalseBitCode(percept);
		return getPercept(falseBitCode, falseBitCode << 1);
	}


	public boolean setPercept(Percept percept, TFUValue value) {
		Objects.requireNonNull(percept);
		int falseBitCode = getPerceptFalseBitCode(percept);
		return setPercept(falseBitCode, falseBitCode << 1, value);
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
		return setPercept(BREEZE_FALSE_BIT, BREEZE_TRUE_BIT, value);
	}


	public boolean setPit(TFUValue value) {
		return setPercept(PIT_FALSE_BIT, PIT_TRUE_BIT, value);
	}


	public boolean setStench(TFUValue value) {
		return setPercept(STENCH_FALSE_BIT, STENCH_TRUE_BIT, value);
	}


	public boolean setWumpus(TFUValue value) {
		return setPercept(WUMPUS_FALSE_BIT, WUMPUS_TRUE_BIT, value);
	}


	public boolean setGlitter(TFUValue value) {
		return setPercept(GLITTER_FALSE_BIT, GLITTER_TRUE_BIT, value);
	}


	public boolean setGold(TFUValue value) {
		return setPercept(GOLD_FALSE_BIT, GOLD_TRUE_BIT, value);
	}


	public boolean setBreeze() {
		return setPercept(BREEZE_FALSE_BIT, BREEZE_TRUE_BIT, TRUE);
	}


	public boolean setPit() {
		return setPercept(PIT_FALSE_BIT, PIT_TRUE_BIT, TRUE);
	}


	public boolean setStench() {
		return setPercept(STENCH_FALSE_BIT, STENCH_TRUE_BIT, TRUE);
	}


	public boolean setWumpus() {
		return setPercept(WUMPUS_FALSE_BIT, WUMPUS_TRUE_BIT, TRUE);
	}


	public boolean setGlitter() {
		return setPercept(GLITTER_FALSE_BIT, GLITTER_TRUE_BIT, TRUE);
	}


	public boolean setGold() {
		return setPercept(GOLD_FALSE_BIT, GOLD_TRUE_BIT, TRUE);
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




	public void copyTo(PerceptCollection other) {
		Objects.requireNonNull(other);

		other.value = this.value;
	}




	@Override
	protected Object clone() throws CloneNotSupportedException {
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
