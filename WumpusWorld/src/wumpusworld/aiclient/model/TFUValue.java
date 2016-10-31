package wumpusworld.aiclient.model;


/**
 * A three-value-type with the values of either TRUE, FALSE or UNDEFINED.
 * Created by Nejc on 12. 10. 2016.
 */
public enum TFUValue {




    FALSE(1),
    UNKNOWN(1 << 1),
    TRUE(1 << 2);




    private static final int FALSE_VALUE = 1;
    private static final int UNKNOWN_VALUE = 1 << 1;
    private static final int TRUE_VALUE = 1 << 2;


    private final int value;




    public int getValue() {
        return value;
    }




    TFUValue(int value) {
        this.value = value;
    }




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


    public static TFUValue fromValue(boolean value) {
        return value
                ? TRUE
                : FALSE;
    }


    public static TFUValue fromValues(boolean isTrue, boolean isFalse) {
        if (isTrue && isFalse)
            throw new IllegalArgumentException("A TFU value cannot be true and false.");

        if (isTrue)
            return TRUE;
        if (isFalse)
            return FALSE;
        return UNKNOWN;
    }




    public TFUValue negate() {
        if (this == TRUE)
            return FALSE;
        if (this == FALSE)
            return TRUE;
        return UNKNOWN;
    }


    public TFUValue and(TFUValue other) {
        if (this == other)
            return this;

        return ((this.value | other.value) & FALSE_VALUE) == FALSE_VALUE
                ? FALSE
                : UNKNOWN;

    }


    public TFUValue or(TFUValue other) {
        if (this == other)
            return this;

        return ((this.value | other.value) & TRUE_VALUE) == TRUE_VALUE
                ? TRUE
                : UNKNOWN;
    }


    public TFUValue imply(TFUValue other) {
        if (this == FALSE || other == TRUE || this == other)
            return TRUE;

        return (this == TRUE && other == FALSE)
                ? FALSE
                : UNKNOWN;
    }


    public boolean equals(boolean value) {
        return value
                ? this == TRUE
                : this == FALSE;
    }


    public boolean isDeterministic() {
        return this != UNKNOWN;
    }


    public boolean isSatisfiable() {
        return this != FALSE;
    }




    @Override
    public String toString() {
        return this == FALSE ? "False"
                : this == UNKNOWN ? "Unknown"
                : "True";
    }


}
