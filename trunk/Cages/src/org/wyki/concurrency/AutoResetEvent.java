package org.wyki.concurrency;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author dominicwilliams
 *
 */
public class AutoResetEvent implements IResetEvent {
	private Semaphore event;
	
	public AutoResetEvent(boolean signalled) {
		event = new Semaphore(signalled ? 1 : 0);
	}
	
	public void set() {
		if (event.hasQueuedThreads() || event.availablePermits() == 0)
			event.release();			
	}
	
	public void reset() {
		event.drainPermits();
	}
	
	public void waitOne() throws InterruptedException {
		event.acquire();
	}
	
	public boolean waitOne(int timeout, TimeUnit unit) throws InterruptedException {
		return event.tryAcquire(timeout, unit);
	}	
	
	public boolean isSignalled() {
		return event.availablePermits() > 0;
	}	
}
