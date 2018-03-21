package main.me.thedome.aesencryptor.utils;

import main.me.thedome.aesencryptor.crypto.CryptoMethods;

/**
 * Package main.me.thedome.aesencryptor.utils
 * Created on 03/2018.
 */

public class PercentageDisplay extends Thread {

	private final Logger logger = Logger.getInstance();
	private boolean running = true; // Whether the thread is running
	private CryptoMethods superClass;

	public PercentageDisplay(CryptoMethods superClass) {
		this.superClass = superClass;
	}

	@Override
	public synchronized void start() {
		super.start();
		running = true;
	}

	@Override
	public void run() {
		while (running) {
			// Log the percent while we are running

			int tmpPer = (int) Math.round(superClass.percentage * 10000) / 100;
			logger.percent(tmpPer);
			try {
				Thread.sleep(1000); // Sleep 200ms to prevent spamming
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopThread() {
		running = false;
	}
}
