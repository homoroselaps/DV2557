package wumpusworld.aiclient.model;


import wumpusworld.aiclient.Percept;

import java.util.Objects;




/**
 * Holds data associated with the {@link PerceptCollection#getPerceptChangedEvent()} event.
 * <p>
 * Created by Nejc on 13. 10. 2016.
 */
public class PerceptChanged {




    private final Percept percept;
    private final TFUValue oldValue;
    private final TFUValue newValue;




    /**
     * Gets the {@link Percept} that has changed.
     *
     * @return The {@link Percept} that has changed.
     */
    public Percept getPercept() {
        return percept;
    }


    /**
     * Gets the old value for the associated percept.
     *
     * @return The old value.
     */
    public TFUValue getOldValue() {
        return oldValue;
    }


    /**
     * Gets the new value of the associated percept that has replaced the old value.
     *
     * @return The new value.
     */
    public TFUValue getNewValue() {
        return newValue;
    }




    /**
     * Creates a new instance of {@link PerceptChanged}.
     *
     * @param percept  Associated percept.
     * @param newValue The old value of the associated percept.
     * @param oldValue The new value of the associated percept that has replaced the odl value.
     */
    public PerceptChanged(Percept percept, TFUValue newValue, TFUValue oldValue) {
        Objects.requireNonNull(percept);
        Objects.requireNonNull(newValue);
        Objects.requireNonNull(oldValue);

        this.percept = percept;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }


}
