package wumpusworld.aiclient.util;


import java.util.Objects;




/**
 * Represents a public side of an event.
 * <p>
 * Created by Nejc on 13. 10. 2016.
 */
public class EventInterface<T> {




    protected final Event<T> event;




    /**
     * Creates a new instance of the {@link EventInterface}.
     *
     * @param event The associated event.
     */
    public EventInterface(Event<T> event) {
        Objects.requireNonNull(event);
        this.event = event;
    }




    /**
     * Subscribes an {@link EventHandler} to the event.
     *
     * @param eventHandler The event handler to subscribe.
     */
    public void subscribe(EventHandler<T> eventHandler) {
        Objects.requireNonNull(eventHandler);
        event.getCallbacks().add(eventHandler);
    }


    /**
     * Subscribes an {@link EventHandler} to the event, if it has not been subscribed already.
     *
     * @param eventHandler The event handler to subscribe.
     * @return {@code true} if the event handler has been subscribed anew, {@code false} if the event handler has already been subscribed.
     */
    public boolean subscribeOnce(EventHandler<T> eventHandler) {
        Objects.requireNonNull(eventHandler);
        if (event.getCallbacks().contains(eventHandler))
            return false;
        event.getCallbacks().add(eventHandler);
        return true;
    }


    /**
     * Unsubscribes an {@link EventHandler} from the event.
     *
     * @param eventHandler The event handler to unsubscribe.
     * @return Whether or not the event handler has been removed.
     */
    public boolean unsubscribe(EventHandler<?> eventHandler) {
        return event.getCallbacks().remove(eventHandler);
    }


    /**
     * Checks if given {@link EventHandler} is subscribed to the event.
     *
     * @param eventHandler The event handler to test.
     * @return Whether or not the given event handler is subscribed to the event.
     */
    public boolean isSubscribed(EventHandler<T> eventHandler) {
        return event.getCallbacks().contains(eventHandler);
    }


}
