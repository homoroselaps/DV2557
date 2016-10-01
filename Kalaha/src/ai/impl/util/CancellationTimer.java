package ai.impl.util;


import java.util.Timer;
import java.util.TimerTask;




/**
 * <p>Cancels an operation after a given delay.</p>
 * <p>Created by Nejc on 25. 09. 2016.</p>
 *
 * @see Cancellable
 */
public class CancellationTimer implements Cancellable {




	private final Timer timer;
	private final Cancellable operation;
	private final long delay;
	private CancellationTimerTask task;
	private boolean running;
	private boolean cancellationPending;




	/**
	 * Gets associated cancellable operation.
	 *
	 * @return Associated operation
	 */
	public Cancellable getOperation() {
		return operation;
	}


	/**
	 * Gets cancellation delay.
	 *
	 * @return Delay
	 */
	public long getDelay() {
		return delay;
	}


	/**
	 * Checks if the timer is running.
	 *
	 * @return Whether or not the timer is running
	 */
	public boolean isRunning() {
		return running;
	}


	/**
	 * Cancels scheduled cancellation.
	 */
	@Override
	public void cancel() {
		this.cancellationPending = true;
		if (task != null)
			task.cancel();
		this.running = false;
		this.cancellationPending = false;
	}


	/**
	 * Checks whether a cancellation of cancellation has been requested. <!-- lol -->
	 *
	 * @return Whether or not a cancellation is pending
	 */
	@Override
	public boolean isCancellationPending() {
		return cancellationPending;
	}




	/**
	 * Creates a new instance of {@link CancellationTimer}.
	 *
	 * @param operation Operation to cancel
	 * @param delay     Cancellation delay
	 */
	public CancellationTimer(Cancellable operation, long delay) {
		this.timer = new Timer();
		this.operation = operation;
		this.delay = delay;
	}




	/**
	 * Starts the timer.
	 */
	public void start() {
		if (running)
			throw new IllegalStateException("Already running.");
		this.running = true;
		this.task = new CancellationTimerTask(this);
		timer.schedule(this.task, delay);
	}








	/**
	 * Represents a {@link CancellationTimer}'s cancellation task.
	 */
	private static class CancellationTimerTask extends TimerTask {


		private final CancellationTimer timer;


		/**
		 * Gets the associated timer.
		 *
		 * @return Associated timer
		 */
		public CancellationTimer getTimer() {
			return timer;
		}


		/**
		 * Creates a new instance of {@link CancellationTimerTask}.
		 *
		 * @param timer Associated timer
		 */
		public CancellationTimerTask(CancellationTimer timer) {
			this.timer = timer;
		}


		/**
		 * Performs the cancellation of {@link CancellationTimerTask#timer}'s operation
		 */
		@Override
		public void run() {
			timer.operation.cancel();
			timer.running = false;
			timer.cancellationPending = false;
			timer.task = null;
		}


	}


}
