package main.me.thedome.aesencryptor.utils;

import main.me.thedome.aesencryptor.classes.DEBUG_MODE;

/**
 * AES_Encryptor/PACKAGE_NAME
 * Created on 11/2016.
 */
public class Logger {

	private static final Logger ourInstance = new Logger();
	private DEBUG_MODE mode;
	private boolean percentage_mode = false;

	private Logger() {
		mode = DEBUG_MODE.MODE_SUPRESS;
		percentage_mode = false;
	}

	public static Logger getInstance() {
		return ourInstance;
	}

	/**
	 * Print an empty line
	 */
	public void emptyln() {
		print("");
	}

	/**
	 * Set the bebus level
	 *
	 * @param mode            The Debug mode
	 * @param percentage_mode Should the percent be displayed
	 */
	public void setDebug(DEBUG_MODE mode, boolean percentage_mode) {
		mode = mode == null ? DEBUG_MODE.MODE_SUPRESS : mode;
		this.mode = mode;
		this.percentage_mode = percentage_mode;
		debugln("Starting Logging Factory with debug level: " + mode.level);
	}

	/**
	 * Debug with the specified mode ...
	 *
	 * @param text The text to debug
	 * @param mode The mode
	 */
	public void debug(String text, DEBUG_MODE mode) {
		if (this.mode.level <= mode.level) {
			debugln(text);
		}
	}

	/**
	 * Debug with the normal text with next line
	 *
	 * @param text The text to output
	 */
	public void debugln(String text) {
		debugln(text, true);
	}

	/**
	 * Debug without next line
	 *
	 * @param text The text to print
	 */
	public void debug(String text) {
		debug(text, true);
	}

	/**
	 * Debug with the text and message
	 *
	 * @param text            The text to print
	 * @param display_message Should the message "[DEBUG]" be displayed
	 */
	public void debug(String text, boolean display_message) {
		if (mode.level >= DEBUG_MODE.MODE_NORMAL.level) {
			if (display_message) {
				print("[DEBUG] " + text, false);
			} else {
				print(text, false);
			}
		}
	}

	/**
	 * Debug with the text and message with next line
	 *
	 * @param text            The text to print
	 * @param display_message Should the message "[DEBUG]" be displayed
	 */
	public void debugln(String text, boolean display_message) {
		if (mode.level >= DEBUG_MODE.MODE_NORMAL.level) {
			if (display_message) {
				print("[DEBUG] " + text, true);
			} else {
				print(text, true);
			}
		}
	}

	/**
	 * Print a message with a next line
	 *
	 * @param text The text to print
	 */
	public void print(String text) {
		print(text, true);
	}

	/**
	 * Print a message with or without next line
	 *
	 * @param text The text to print
	 * @param line Should there be a neq line after?
	 */
	public void print(String text, boolean line) {
		if (line) {
			System.out.println(text);
		} else {
			System.out.print(text);
		}
	}

	/**
	 * Print a percent value
	 *
	 * @param percent The value of percent finished to max of 100
	 * @throws IllegalArgumentException If the percent are higher than 100
	 */
	public void percent(double percent) throws IllegalArgumentException {
		percent(percent, false);
	}

	/**
	 * Print a percent value
	 *
	 * @param percent The value of percent finished to max of 100
	 * @param force   Force the value to be printed
	 *
	 * @throws IllegalArgumentException If the percent are higher than 100
	 */
	public void percent(double percent, boolean force) {

		if (percent > 100) throw new IllegalArgumentException("Percent my not be greater than 100!");
		if (!force) {
			if (!percentage_mode) {
				if (mode.level < DEBUG_MODE.MODE_NORMAL.level) return;
			}
		}

		// Erease line
		print("\r", false);

		String hashtags = "";
		for (int i = 0; i < (percent / 10); i++) {
			hashtags += "#";
		}
		while (hashtags.length() < 10) {
			hashtags += " ";
		}

		if (percentage_mode) {
			print("[" + hashtags + "] " + percent + "% of process complete ...", false);
		} else {
			print("[DEBUG] [" + hashtags + "] " + percent + "% of process complete ...", false);
		}
	}

}
