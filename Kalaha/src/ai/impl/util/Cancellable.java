package ai.impl.util;


/**
 * <p>Represents a cancellable operation.</p>
 * <p>Created by Nejc on 25. 09. 2016.</p>
 */
public interface Cancellable {


	/**
	 * Cancels an ongoing operation.
	 */
	void cancel();


	/**
	 * Checks if a cancellation has been requested.
	 *
	 * @return Whether or not a cancellation is pending
	 */
	boolean isCancellationPending();


}
