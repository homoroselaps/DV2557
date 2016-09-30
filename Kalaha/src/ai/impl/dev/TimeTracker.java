package ai.impl.dev;


/**
 *
 * Created by Nejc on 30. 09. 2016.
 */
public class TimeTracker {


	public final static TimeTracker PUBLIC_INSTANCE = new TimeTracker();


	private boolean state;
	private long nanoTime;




	public void start() {
		if(state)
			throw new IllegalStateException("Already running.");
		state = true;

		nanoTime = System.nanoTime();
	}


	public long stop() {
		if(!state)
			throw new IllegalStateException("Already stopped.");
		state = false;

		return System.nanoTime() - nanoTime;
	}


	public long stopPrint() {
		long value = stop();
		System.out.println("Elapsed time: " + (value / 1000L / 1000L) + "ms");
		return value;
	}


}
