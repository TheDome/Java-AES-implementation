package main.me.thedome.aesencryptor.utils;

import main.me.thedome.aesencryptor.classes.DEBUG_MODE;
import main.me.thedome.aesencryptor.main.AESEncryptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * AES_Encryptor/main.me.thedome.aesencryptor.utils
 * Created on 11/2016.
 * <p>
 * This class manages all the Files and IO stuff. Rendered in an other thread
 */
public class IOUtils extends Thread {

	private static IOUtils ourInstance = new IOUtils();
	// This class manages the File stuff
	// All the Files
	public File inputfile;
	public File keyfile;
	public File outputfile;
	Logger logger = Logger.getInstance();
	AESEncryptor enc;

	// The Standarts
	FileOutputStream out;

	private IOUtils() {
		logger = Logger.getInstance();
		this.start();
	}

	public static IOUtils getInstance() {
		return ourInstance;
	}

	/**
	 * This Method should be called First
	 *
	 * @param enc The Main Class
	 */
	public void setAEC(AESEncryptor enc) {
		this.enc = enc;
		try {
			this.out = new FileOutputStream(outputfile, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setOut() {
		try {
			this.out = new FileOutputStream(outputfile, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void killOut() {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This Function reads all the bytes of a File
	 *
	 * @return The bytes of the File
	 */
	public byte[] readInput(File file) {

		if (!file.exists()) {
			logger.print("Failed to find: " + file.getAbsolutePath());
			try {
				throw new FileNotFoundException("You have to Specify the correct filefor input!");
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		byte[] b = null;

		// The path for the File
		Path p = Paths.get(file.getAbsolutePath());
		try {
			b = Files.readAllBytes(p);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}


		return b;
	}

	/**
	 * This Function reads all the bytes of the normal inputfile
	 */
	public byte[] readInput() {
		return readInput(inputfile);
	}

	/**
	 * This Method writes the bytes to the normal Output
	 *
	 * @param bytes The bytes to be written
	 */
	public void writeOutput(byte[] bytes) {
		writeOutput(bytes, outputfile);
	}

	/**
	 * This function writes all the Bytes to a File
	 *
	 * @param bytes  The bytes from the methods
	 * @param output The File to output
	 */
	public void writeOutput(byte[] bytes, File output) {

		logger.debug("Writing file to: " + output.getAbsolutePath());
		// The path for the File
		Path p = Paths.get(output.getAbsolutePath());
		try {
			Files.write(p, bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write a chunk of a File to the File. This is a bit slower, than flush all, but is saves memory;
	 *
	 * @param data The data to write to the File
	 * @param file The File to be written
	 */
	public void writeChunkedOutput(byte[] data, File file) {

		try {
			if (!file.exists()) file.createNewFile();

			out.write(data);

			// Flush all to the File
			out.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Write a chunk of a File to the normal output
	 *
	 * @param data The data to write to the File
	 */
	public void writeChunkedOutput(byte[] data) {

		writeChunkedOutput(data, outputfile);

	}

	/**
	 * Write the key to the File for it
	 *
	 * @param key The Key as an Array
	 */
	public void writeKey(byte[][] key) {

		// The bytes to output later
		byte[] out = new byte[enc.KEYSIZE];
		logger.print("Saving Key ...");

		// The index
		int i = 0;

		for (byte[] b : key) {
			for (byte b1 : b) {
				out[i++] = b1;
			}
		}

		logger.debug("Writing key to File: " + keyfile.getAbsolutePath(), DEBUG_MODE.MODE_NORMAL);
		IOUtils.getInstance().writeOutput(out, keyfile);

	}
}
