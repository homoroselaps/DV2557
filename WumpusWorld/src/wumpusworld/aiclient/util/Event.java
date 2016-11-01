package wumpusworld.aiclient.util;


import java.util.ArrayList;
import java.util.List;




/**
 * Represents an event with a callback subscription list that.
 * This class is not thread-safe.
 * <p>
 * Created by Nejc on 13. 10. 2016.
 */
public class Event<T> {




    private final List<EventHandler<T>> callbacks;




    /**
     * Gets a list of all subscribed {@link EventHandler}s.
     *
     * @return The subscription list.
     */
    public List<EventHandler<T>> getCallbacks() {
        return callbacks;
    }




    /**
     * Creates a new instance of the {@link Event}.
     */
    public Event() {
        callbacks = new ArrayList<>();
    }


    /**
     * Creates a new instance of the {@link Event}.
     *
     * @param capacity Expected capacity of the subscription list.
     */
    public Event(int capacity) {
        callbacks = new ArrayList<>(capacity);
    }




    /**
     * Gets the publicly visible side of the event.
     *
     * @return The event's public interface.
     */
    public EventInterface<T> getInterface() {
        return new EventInterface<>(this);
    }


    /**
     * Invokes all subscribed {@link EventHandler}s.
     *
     * @param sender The object that fired the event.
     * @param args   The arguments associated with the event.
     */
    public void invoke(Object sender, T args) {
        EventHandler<T>[] eventHandlers = new EventHandler[callbacks.size()]; // create a clone
        callbacks.toArray(eventHandlers);

        for (EventHandler<T> eventHandler : eventHandlers)
            if (eventHandler != null)
                eventHandler.onEvent(sender, args);
    }


    /**
     * Invokes all subscribed {@link EventHandler}s and catches any exceptions.
     *
     * @param sender The object that fired the event.
     * @param args   The arguments associated with the event.
     * @return Whether or not all subscribers have been successfully invoked.
     */
    public boolean invokeSafe(Object sender, T args) {
        boolean successful = true;
        for (EventHandler<T> callback : callbacks) {
            if (callback != null) {
                try {
                    callback.onEvent(sender, args);
                } catch (Exception ex) {
                    successful = false;
                }
            }
        }
        return successful;
    }


}
