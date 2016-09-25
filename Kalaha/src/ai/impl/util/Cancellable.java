package ai.impl.util;


/**
 * Represents a cancellable action.
 * Created by Nejc on 25. 09. 2016.
 */
public interface Cancellable {


	void cancel();


	boolean isCancellationPending();


}
