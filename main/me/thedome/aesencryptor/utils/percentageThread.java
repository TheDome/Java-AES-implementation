package main.me.thedome.aesencryptor.utils;

import main.me.thedome.aesencryptor.crypto.CryptoMethods;

/**
 * Package main.me.thedome.aesencryptor.utils
 * Created on 03/2018.
 */

public class percentageThread extends Thread {

	private final Logger logger = Logger.getInstance();
	boolean running; // Whether the thread is running
	private CryptoMethods superClass;

	public percentageThread(CryptoMethods superClass) {
		this.superClass = superClass;
	}

	@Override
	public void run() {
		while (running) {
			// Log the percent while we are running
			logger.percent(superClass.percentage);
			try {
				Thread.sleep(200); // Sleep 200ms to prevent spamming
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopThread() {
		running = false;
	}
}
