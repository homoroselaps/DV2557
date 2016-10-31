package wumpusworld.aiclient.util;


import java.util.Objects;




/**
 * Represents a public side of an event.
 * Created by Nejc on 13. 10. 2016.
 */
public class EventInterface<T> {




    private final Event<T> event;




    public EventInterface(Event<T> event) {
        Objects.requireNonNull(event);
        this.event = event;
    }




    public void subscribe(EventHandler<T> eventHandler) {
        Objects.requireNonNull(eventHandler);
        event.getCallbacks().add(eventHandler);
    }


    public boolean subscribeOnce(EventHandler<T> eventHandler) {
        Objects.requireNonNull(eventHandler);
        if (event.getCallbacks().contains(eventHandler))
            return false;
        event.getCallbacks().add(eventHandler);
        return true;
    }


    public boolean unsubscribe(EventHandler<?> eventHandler) {
        return event.getCallbacks().remove(eventHandler);
    }


    public boolean isSubscribed(EventHandler<T> eventHandler) {
        return event.getCallbacks().contains(eventHandler);
    }


}
