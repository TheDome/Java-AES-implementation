package main.me.thedome.aesencryptor.crypto;

import main.me.thedome.aesencryptor.classes.DEBUG_MODE;
import main.me.thedome.aesencryptor.utils.IOUtils;
import main.me.thedome.aesencryptor.utils.Logger;
import main.me.thedome.aesencryptor.utils.PercentageDisplay;

import java.util.ArrayList;
import java.util.Random;

/**
 * AES_Encryptor/PACKAGE_NAME
 * Created on 11/2016.
 */
public class CryptoMethods {

	private final Logger logger = Logger.getInstance();
	// The percentage of the current operation
	public double percentage = 1;
	private IOUtils iostuff;
	// A thread to print the current percentage
	private PercentageDisplay percentageThread = new PercentageDisplay(this);

	public CryptoMethods() {
		super();
		iostuff = IOUtils.getInstance();
	}

	/**
	 * Encrypt the byte[]
	 *
	 * @param in  The bytes to be encrypted
	 * @param key The key used for encryption
	 */
	public void encrypt(byte[] in, byte[][] key) {

		// We only need 1024 bytes cause every 1024 Bytes we write all to the File
		byte[] encrypted = new byte[1024];

		// Notify the user
		logger.print("Starting encryption ...");

		byte upper;
		byte lower;
		// The index
		int i = 0;
		// The iterations of the loop
		int loopIters = 0;

		percentageThread.start(); // Start the output of the current percentage

		// Open the outputStream
		iostuff.setOut();

		logger.debug("", false);
		logger.debugln("Encrypting: ...");
		// All the bytes in the array will be encrypted
		for (byte b : in) {

			// we have encrypted one KB write it
			if (i == 1024) {
				iostuff.writeChunkedOutput(encrypted);
				i = 0;
			}

			// The upper 4 bits of the byte
			// 0100 0110 -> 0100 0000 -> 0000 0100
			upper = (byte) ((b & 0xF0) >> 4);

			// The lower 4 bits of the byte
			// 0100 0110 -> 0000 0110
			lower = (byte) (b & 0x0F);

			// Swap the with the key bytes
			encrypted[i++] = key[upper][lower];

			// Compute the percentage rate of the process
			percentage = loopIters++ / (in.length * 1.0);

			/*// Print at least 0, 25, 50, 75, 100%
			double roundedPercentage = Math.rint(percentage * 10000) / 100;
			int tmpPer = (int) Math.round(roundedPercentage * 100) / 100;
			if (percentage % 0.25 == 0 && tmpPer <= 100 && tmpPer != 0) {
				logger.percent(tmpPer, true);
			}*/

		}

		// Write the last encrypted bytes to the File
		iostuff.writeChunkedOutput(encrypted);

		percentageThread.stopThread(); // Stop the output of the percentage

		logger.debug("", false);
		logger.print("Finished!");

		// Close the outputstream
		iostuff.killOut();
	}

	/**
	 * Method to decrypt all with the key
	 *
	 * @param in  The Input bytes
	 * @param key The Key used to encrypt
	 */
	public void decrypt(byte[] in, byte[][] key) {

		logger.print("Starting decryption ...");
		// The output
		byte[] output = new byte[in.length];


		// The index 0 the loop
		int i = 0;
		// The upper 4 bits
		byte upper;
		// The lower 4 bits
		byte lower;

		// Should we exit?
		boolean exit;

		percentageThread.start();

		logger.debug("Decrypting " + in.length + " Bytes ...", DEBUG_MODE.MODE_NORMAL);
		logger.print("");
		logger.debugln("Decrypting: ");

		for (byte b : in) {
			exit = false;
			for (int x = 0; x < key.length; x++) {
				for (int y = 0; y < key[x].length; y++) {
					if (b == key[x][y]) {
						// Exit the next loop
						exit = true;

						// We found the equivalent bytes in the grid....
						upper = (byte) (x << 4);
						lower = (byte) y;

						output[i++] = (byte) (upper | lower);

						// we have encrypted one KB write it
						if (i == 1024) {
							iostuff.writeChunkedOutput(output);
							i = 0;
						}

						/*
						 * all the percentage handeling
						 */

						percentage = i / (in.length * 1.0);

						/*// Print at least 0, 25, 50, 75, 100%
						int tmpPer = (int) Math.round(percentage * 100);
						if (tmpPer == 0 || tmpPer == 25 || tmpPer == 50 || tmpPer == 75 || tmpPer == 100) {
							logger.percent(tmpPer, true);
						}*/

						break;

					}
				}

				// We found, what we needed ... exit
				if (exit) break;
			}

		}

		percentageThread.stopThread(); // Stop the output of the percentage

		// Write the last decrypted bytes to the File
		iostuff.writeChunkedOutput(output);

		logger.debug("", DEBUG_MODE.MODE_ALL);
		logger.print("Finished ...");
	}

	/**
	 * Generate the key for the encryption
	 *
	 * @param keysize The size of the key. Please do not edit. Some fixes needed
	 */
	public byte[][] genKey(int keysize) {

		logger.print("Generating " + keysize + " Bit key ...");
		// the size iof every dimension
		int dimsize = (int) Math.sqrt(keysize);
		byte[][] key = new byte[dimsize][dimsize];
		// The RNG to generate
		Random rnd = new Random();

		// How far is the generation?
		double percent;
		int i = 0;

		// Byte unique
		boolean unique;

		// To uniquify the key
		ArrayList<Byte> keybytes = new ArrayList<>();

		logger.debugln("");
		logger.debugln("Generating key: ");

		for (int x = 0; x < dimsize; x++) {
			for (int y = 0; y < dimsize; y++) {

				// next run ...
				unique = true;

				// Get every single content
				byte[] byte1 = new byte[1];

				while (unique) {
					rnd.nextBytes(byte1);

					unique = keybytes.contains(byte1[0]);
					keybytes.add(byte1[0]);
				}

				key[x][y] = byte1[0];

				percent = ((double) Math.round(((double) i++ / (double) keysize) * 10000) / 100);


				// Print the percent to the debug console
				logger.percent(percent);


			}
		}
		// End of the for loop

		return key;
	}
}
