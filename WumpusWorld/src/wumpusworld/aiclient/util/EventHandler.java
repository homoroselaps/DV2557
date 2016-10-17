package wumpusworld.aiclient.util;


/**
 * Represents an event callback declaration.
 * Created by Nejc on 13. 10. 2016.
 */
@FunctionalInterface
public interface EventHandler<T> {


	void onEvent(Object sender, T arg);


}
