package wumpusworld.aiclient.model;


import wumpusworld.aiclient.Percept;

import java.util.Objects;




/**
 * Holds data associated with the perceptChanged event in the {@link PerceptCollection}.
 * Created by Nejc on 13. 10. 2016.
 */
public class PerceptChanged {




	private final Percept percept;
	private final TFUValue previousValue;




	public Percept getPercept() {
		return percept;
	}


	public TFUValue getPreviousValue() {
		return previousValue;
	}




	public PerceptChanged(Percept percept, TFUValue previousValue) {
		Objects.requireNonNull(percept);
		Objects.requireNonNull(previousValue);

		this.percept = percept;
		this.previousValue = previousValue;
	}


}
