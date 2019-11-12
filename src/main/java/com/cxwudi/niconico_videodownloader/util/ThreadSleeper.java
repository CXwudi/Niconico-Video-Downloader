package com.cxwudi.niconico_videodownloader.util;

/**
 * A class that allows calling Thread.sleep() without writing try catch statement in order to achieve better code readability,
 * @warn don't use it in multi-thread program
 * @author CX无敌
 *
 */
public interface ThreadSleeper {
	
	public static boolean sleep(long millis) throws InterruptedException {
		
			Thread.sleep(millis);
			return true;
		
	}
}
