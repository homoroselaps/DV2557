package wumpusworld.aiclient.util;


import java.util.ArrayList;
import java.util.List;




/**
 * Represents an invokable event.
 * Created by Nejc on 13. 10. 2016.
 */
public class Event<T> {




	private final List<EventHandler<T>> callbacks;




	public List<EventHandler<T>> getCallbacks() {
		return callbacks;
	}




	public Event() {
		callbacks = new ArrayList<>();
	}


	public Event(int capacity) {
		callbacks = new ArrayList<>(capacity);
	}




	public EventInterface<T> getInterface() {
		return new EventInterface<>(this);
	}


	public void invoke(Object sender, T args) {
		for (EventHandler<T> callback : callbacks) {
			if (callback != null)
				callback.onEvent(sender, args);
		}
	}


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
