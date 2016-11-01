package wumpusworld.aiclient.model;


/**
 * A three-value-type with the values of either TRUE, FALSE or UNDEFINED.
 * <p>
 * Created by Nejc on 12. 10. 2016.
 */
public enum TFUValue {




    /**
     * Represents absence the FALSE state.
     */
    FALSE(1),
    /**
     * Represents an state, which cannot be determined as either TRUE or FALSE.
     */
    UNKNOWN(1 << 1),
    /**
     * Represents the TRUE state.
     */
    TRUE(1 << 2);




    private static final int FALSE_VALUE = 1;
    private static final int UNKNOWN_VALUE = 1 << 1;
    private static final int TRUE_VALUE = 1 << 2;


    private final int value;



    /**
     * Gets an integer representation of the value.
     *
     * @return An integer representation of the value.
     */
    public int getValue() {
        return value;
    }




    TFUValue(int value) {
        this.value = value;
    }




    /**
     * Converts boolean to TFUValue.
     *
     * @param value Value to convert.
     * @return FALSE for 1, UNKNOWN for 2, and TRUE for 4, otherwise throws an {@link IllegalArgumentException}
     */
    public static TFUValue fromValue(int value) {
        switch (value) {
            case FALSE_VALUE:
                return FALSE;
            case UNKNOWN_VALUE:
                return UNKNOWN;
            case TRUE_VALUE:
                return TRUE;
            default:
                throw new IllegalArgumentException();
        }
    }


    /**
     * Converts a boolean value to a TFUValue. Note that UNKNOWN cannot be returned.
     *
     * @param value Value to convert.
     * @return {@link TFUValue#TRUE} for {@code true} or {@link TFUValue#FALSE} for {@code false}.
     */
    public static TFUValue fromValue(boolean value) {
        return value
                ? TRUE
                : FALSE;
    }


    /**
     * Converts two boolean values into a TFUValue.
     *
     * @param isTrue  Whether the value should be TRUE.
     * @param isFalse Whether the value should be FALSE:
     * @return TRUE if only isTrue is true, FALSE if only isFalse is true, UNKNOWN if both arguments are {@code false}, otherwise throws an {@link IllegalArgumentException}.
     */
    public static TFUValue fromValues(boolean isTrue, boolean isFalse) {
        if (isTrue && isFalse)
            throw new IllegalArgumentException("A TFU value cannot be true and false.");

        if (isTrue)
            return TRUE;
        if (isFalse)
            return FALSE;
        return UNKNOWN;
    }




    /**
     * Negates the current value.
     *
     * @return The negation of the current value.
     */
    public TFUValue negate() {
        if (this == TRUE)
            return FALSE;
        if (this == FALSE)
            return TRUE;
        return UNKNOWN;
    }


    /**
     * Gets a conjunction of two values.
     *
     * @param other The other value.
     * @return A conjunction of the two values.
     */
    public TFUValue and(TFUValue other) {
        if (this == other)
            return this;

        return ((this.value | other.value) & FALSE_VALUE) == FALSE_VALUE
                ? FALSE
                : UNKNOWN;

    }


    /**
     * Gets a disjunction of two values.
     *
     * @param other The other value.
     * @return A disjunction of the two values.
     */
    public TFUValue or(TFUValue other) {
        if (this == other)
            return this;

        return ((this.value | other.value) & TRUE_VALUE) == TRUE_VALUE
                ? TRUE
                : UNKNOWN;
    }


    /**
     * Gets the result of an implication to a given value. This instance is the premise of the implication.
     *
     * @param other The conclusion of the implication.
     * @return
     */
    public TFUValue imply(TFUValue other) {
        if (this == FALSE || other == TRUE || this == other)
            return TRUE;

        return (this == TRUE && other == FALSE)
                ? FALSE
                : UNKNOWN;
    }


    /**
     * Checks if this value equals to a boolean value.
     *
     * @param value The value to test.
     * @return true if TRUE, false if FALSE.
     */
    public boolean equals(boolean value) {
        return value
                ? this == TRUE
                : this == FALSE;
    }


    /**
     * Checks if value is either TRUE or FALSE.
     *
     * @return Whether or not the value is deterministic.
     */
    public boolean isDeterministic() {
        return this != UNKNOWN;
    }


    /**
     * Checks if the value is either TRUE or UNDEFINED.
     *
     * @return Whether or not the value is satisfiable.
     */
    public boolean isSatisfiable() {
        return this != FALSE;
    }




    /**
     * Gets the string representation of this value.
     *
     * @return The string representation.
     */
    @Override
    public String toString() {
        return this == FALSE ? "False"
                : this == UNKNOWN ? "Unknown"
                : "True";
    }


}
