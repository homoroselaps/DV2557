package ai.impl.util;


import java.util.Timer;
import java.util.TimerTask;




/**
 * Cancels an operation, when needed.
 * Created by Nejc on 25. 09. 2016.
 */
public class CancellationTimer implements Cancellable {


	private final Timer timer;
	private final Cancellable item;
	private final long delay;
	private CancellationTimerTask task;
	private boolean running;
	private boolean cancellationPending;




	public Cancellable getItem() {
		return item;
	}


	public long getDelay() {
		return delay;
	}


	public boolean isRunning() {
		return running;
	}


	public void cancel() {
		this.cancellationPending = true;
		if (task != null)
			task.cancel();
		this.running = false;
		this.cancellationPending = false;
	}


	@Override
	public boolean isCancellationPending() {
		return cancellationPending;
	}




	public CancellationTimer(Cancellable item, long delay) {
		this.timer = new Timer();
		this.item = item;
		this.delay = delay;
	}




	public void start() {
		if (running)
			throw new IllegalStateException("Already running.");
		this.running = true;
		this.task = new CancellationTimerTask(this);
		timer.schedule(this.task, delay);
	}








	private static class CancellationTimerTask extends TimerTask {


		private final CancellationTimer timer;


		public CancellationTimer getTimer() {
			return timer;
		}


		public CancellationTimerTask(CancellationTimer timer) {
			this.timer = timer;
		}


		@Override
		public void run() {
			timer.item.cancel();
			timer.running = false;
			timer.cancellationPending = false;
			timer.task = null;
		}


	}


}
