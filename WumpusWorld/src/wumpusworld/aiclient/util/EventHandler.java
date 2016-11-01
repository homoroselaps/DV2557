package wumpusworld.aiclient.util;


/**
 * Represents an event callback declaration.
 * <p>
 * Created by Nejc on 13. 10. 2016.
 */
@FunctionalInterface
public interface EventHandler<T> {


    /**
     * Called when an event is fired.
     *
     * @param sender The object that called for the event.
     * @param arg    The argument associated with the event.
     */
    void onEvent(Object sender, T arg);


}
