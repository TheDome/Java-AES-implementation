package main.me.thedome.aesencryptor.main;

import main.me.thedome.aesencryptor.classes.DEBUG_MODE;
import main.me.thedome.aesencryptor.classes.OPERATION_MODE;
import main.me.thedome.aesencryptor.crypto.CryptoMethods;
import main.me.thedome.aesencryptor.utils.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is a simple definition of the AES standart. It should de- and
 * encrypt a File
 *
 * @author thedome
 *
 */
public class AESEncryptor {

	// The size of the key Must be an exponent of 2
	private final int KEYSIZE = 256;
	private final CryptoMethods methods = new CryptoMethods();
	private final Logger logger = Logger.getInstance();
	private boolean percentage_mode = false;
	private OPERATION_MODE mode;
	private byte[][] key;
	// All the Files
	private File inputfile;
	private File keyfile;
	private boolean keyfile_exists;
	private File outputfile;

	private AESEncryptor(String[] args) throws FileNotFoundException {

		parseArgs(args);

		// Test if all arguments passed ....
		if (inputfile == null) {
			try {
				inputfile = new File(args[0]);

				if (!inputfile.exists()) {
					System.out.println("You have to specify a File for input!");
					printUsage();
					System.exit(1);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mode = mode == null ? OPERATION_MODE.MODE_ENCRYPT : mode;

		if (keyfile == null) keyfile = new File(inputfile.getAbsolutePath() + ".aeskey");

		logger.print("Starting process ...");
		if (mode == OPERATION_MODE.MODE_ENCRYPT) {
			logger.debugln("Selected mode: encryption");

			if (outputfile == null) {
				outputfile = new File(inputfile.getAbsolutePath() + ".enc");
			}
		} else {
			logger.debugln("Selected mode: decryption");

			if (outputfile == null) {
				outputfile = new File(inputfile.getAbsolutePath().substring(0, inputfile.getAbsolutePath().length() - 4));
			}
		}
		logger.debugln("With keyfile: " + keyfile.getAbsolutePath());
		logger.print("With input File: " + inputfile.getAbsolutePath());
		logger.print("To: " + outputfile.getAbsolutePath());

		if (mode == OPERATION_MODE.MODE_ENCRYPT) {

			// If the keyfile exists
			if(keyfile.exists()){
				// Read the key in ...
				readKey(keyfile);
				// Tell the system, that we use one ...
				keyfile_exists = true;
				logger.debugln("Found preexisting key");
			} else { // else
				// Generate the key
				key = methods.genKey(KEYSIZE);
			}


			if (!keyfile_exists) writeKey(key);
			byte[] input = readInput(inputfile);
			byte[] encrypted = methods.encrypt(input, key);
			writeOutput(encrypted, outputfile);

		} else { // Decryption mode
			// Read the key
			System.out.println(keyfile.exists());
			if (!keyfile.exists()) {
				throw new FileNotFoundException("The Keyfile specified cant be found");
			}
			// Read the key in
			readKey(keyfile);

			byte[] input = readInput(inputfile);
			byte[] encrypted = methods.decrypt(input, key);
			writeOutput(encrypted, outputfile);
		}

	}

	public static void main(String[] args) throws Exception {
		new AESEncryptor(args);
	}

	private void parseArgs(String[] args) {

		if (args.length < 1) {
			System.out.println("You have to specify at least 1 Arguments: input File");
			printUsage();
			System.exit(1);
		}

		// Check arguments
		for (int i = 0; i < args.length; i++) {

			switch (args[i]) {
				case "-v":
					boolean debug = true;
					break;
				case "-k":
					keyfile = new File(args[i + 1]);
					break;
				case "-e":
					mode = OPERATION_MODE.MODE_ENCRYPT;
					break;
				case "-d":
					mode = OPERATION_MODE.MODE_DECRYPT;
					break;
				case "-i":
					inputfile = new File(args[i + 1]);
					break;
				case "-o":
					outputfile = new File(args[i + 1]);
					break;
				case "-h":
					printUsage();
					System.exit(1);
					break;
				case "-p":


				default:
					break;
			}
		}
	}

	private void printUsage() {

		System.out.println("Usage:");
		System.out.println("\t-i input file");
		System.out.println("\t-o output file");
		System.out.println("\t-e encryption mode");
		System.out.println("\t-d decyption mode");
		System.out.println("\t-k keyfile");
		System.out.println("\t-h display this help");
		System.out.println("\t-p Enable the percentage display mode (may not be enabled due the debug mode)");
		System.out.println("\t[optional] -v verbose");

	}

	private void readKey(File keyfile) {
		// the size iof every dimension
		int dimsize = (int) Math.sqrt(keyfile.length());

		byte[][] aes_key = new byte[dimsize][dimsize];
		byte[] keybytes = readInput(keyfile);

		// The index
		int i = 0;

		for (int ax = 0; ax < aes_key.length; ax++){
			for(int ay = 0; ay < aes_key[ax].length; ay++){
				aes_key[ax][ay] = keybytes[i++];
			}
		}

		this.key = aes_key;
	}

	private void writeKey(byte[][] key) {

		// The bytes to output later
		byte[] out = new byte[KEYSIZE];
		logger.print("Saving Key ...");

		// The index
		int i = 0;

		for(byte[] b : key){
			for (byte b1 : b){
				out[i++] = b1;
			}
		}

		logger.debug("Writing key to File: " + keyfile.getAbsolutePath(), DEBUG_MODE.MODE_NORMAL);
		writeOutput(out, keyfile);

	}


	
	/**
	 * This Function reads all the bytes of a File
	 * @return The bytes of the File
	 */
	private byte[] readInput(File file) {
		byte[] b = null;

		// The path for the File
		Path p = Paths.get(file.getAbsolutePath());
		try {
			b = Files.readAllBytes(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}


		return b;
	}

	/**
	 * This function writes all the Bytes to a File
	 * @param bytes The bytes from the methods
	 * @param output The File to output
	 */
	private void writeOutput(byte[] bytes, File output) {

		logger.debug("Writing file to: " + output.getAbsolutePath());
		// The path for the File
		Path p = Paths.get(output.getAbsolutePath());
		try {
			Files.write(p, bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
