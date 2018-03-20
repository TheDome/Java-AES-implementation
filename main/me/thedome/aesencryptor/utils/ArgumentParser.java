package main.me.thedome.aesencryptor.utils;

import main.me.thedome.aesencryptor.classes.DEBUG_MODE;
import main.me.thedome.aesencryptor.classes.OPERATION_MODE;
import main.me.thedome.aesencryptor.main.AESEncryptor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * AES_Encryptor/PACKAGE_NAME
 * Created on 11/2016.
 */
public class ArgumentParser {


	private final ArrayList<String> args;
	private int index = 0;
	private AESEncryptor aec;
	private IOUtils utils = IOUtils.getInstance();


	public ArgumentParser(String[] args, AESEncryptor encclass) {
		this.args = new ArrayList<>();
		Collections.addAll(this.args, args);
		aec = encclass;
		pepareArguments();
	}

	/**
	 * Print the Help and exit then
	 */
	public static void printHelp() {

		System.out.println("Usage: [name] inputfile for an only encryption");
		System.out.println("Usage: [name] -i inputfile -k key for an encryption with a key");
		System.out.println("Usage: [name] -i inputfile -k key -d for a decryption with a key");
		System.out.println("\nOther parameters to pass:");
		System.out.println("\t-i input file");
		System.out.println("\t-o output file");
		System.out.println("\t-e encryption mode");
		System.out.println("\t-d decyption mode");
		System.out.println("\t-k keyfile");
		System.out.println("\t-h display this help");
		System.out.println("\t-p Enable the percentage display mode (may not be enabled due the debug mode)");
		System.out.println("\t[optional] -v [optional] level (0,1,2) Enable verbose mode with level (default:1)");

		System.exit(1);

	}

	/**
	 * Watch through the Arguments and draw first conclusions
	 */
	private void pepareArguments() {
		if (args.contains("-h")) {
			printHelp();
		}
		if (args.size() < 1) {
			printHelp();
		}
	}

	public void pushBack() {
		this.index--;
	}

	/**
	 * Parse the Arguments given to the Object
	 */
	public void parseArgs() {
		checkForSingleArgs();
		// Check arguments
		while (hasNextArgument()) {
			switch (nextArgument()) {
				case "-v":
					int level;
					try {
						level = Integer.parseInt(nextArgument());
					} catch (Exception e) {
						pushBack();
						level = DEBUG_MODE.MODE_NORMAL.level;
					}

					aec.debMode = DEBUG_MODE.MODE_NORMAL.getByLevel(level);
					break;
				case "-k":
					utils.keyfile = new File(nextArgument());
					break;
				case "-e":
					aec.mode = OPERATION_MODE.MODE_ENCRYPT;
					break;
				case "-d":
					aec.mode = OPERATION_MODE.MODE_DECRYPT;
					break;
				case "-i":
					utils.inputfile = new File(nextArgument());
					break;
				case "-o":
					utils.outputfile = new File(nextArgument());
					break;
				case "-h":
					printHelp();
					break;
				case "-p":
					aec.percentage_mode = true;
					break;
				case "--version":
					Logger.getInstance().print("Current Version:");
					Logger.getInstance().print("\t" + aec.VERSION);
					System.exit(1);
					break;

				default:
					break;
			}
		}
	}

	private void checkForSingleArgs() {
		if (args.size() == 1) {
			utils.inputfile = new File(args.get(0));
		}
	}

	/**
	 * Get the next argument
	 *
	 * @return The next argument
	 */
	public String nextArgument() {
		return args.get(index++);
	}

	/**
	 * Returnst, if we have Arguments left
	 *
	 * @return If we have an Argument left
	 */
	public boolean hasNextArgument() {
		return index < args.size();
	}
}
