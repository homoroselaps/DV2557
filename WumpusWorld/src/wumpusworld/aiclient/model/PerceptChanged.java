package wumpusworld.aiclient.model;


import wumpusworld.aiclient.Percept;

import java.util.Objects;




/**
 * Holds data associated with the perceptChanged event in the {@link PerceptCollection}.
 * Created by Nejc on 13. 10. 2016.
 */
public class PerceptChanged {




	private final Percept percept;
	private final TFUValue oldValue;
	private final TFUValue newValue;




	public Percept getPercept() {
		return percept;
	}


	public TFUValue getOldValue() {
		return oldValue;
	}


	public TFUValue getNewValue() {
		return newValue;
	}




	public PerceptChanged(Percept percept, TFUValue newValue, TFUValue oldValue) {
		Objects.requireNonNull(percept);
		Objects.requireNonNull(newValue);
		Objects.requireNonNull(oldValue);

		this.percept = percept;
		this.newValue = newValue;
		this.oldValue = oldValue;
	}


}
