package main.me.thedome.aesencryptor.main;

import main.me.thedome.aesencryptor.classes.DEBUG_MODE;
import main.me.thedome.aesencryptor.classes.OPERATION_MODE;
import main.me.thedome.aesencryptor.crypto.CryptoMethods;
import main.me.thedome.aesencryptor.utils.ArgumentParser;
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

	public final String VERSION = "1.0.1";
	// The size of the key Must be an exponent of 2
	private final int KEYSIZE = 256;
	private final CryptoMethods methods = new CryptoMethods();
	private final Logger logger = Logger.getInstance();

	public boolean percentage_mode = false;
	public DEBUG_MODE debMode;

	public OPERATION_MODE mode;
	// All the Files
	public File inputfile;
	public File keyfile;
	public File outputfile;
	private byte[][] key;
	private boolean keyfile_exists;

	private AESEncryptor(String[] args) throws FileNotFoundException {


		ArgumentParser parser = new ArgumentParser(args, this);
		parser.parseArgs();

		logger.setDebug(debMode, percentage_mode);

		// Test if all arguments passed ....
		if (inputfile == null) {
			try {
				inputfile = new File(args[0]);

				if (!inputfile.exists()) {
					System.out.println("You have to specify a File for input!");
					ArgumentParser.printHelp();
					System.exit(1);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mode = mode == null ? OPERATION_MODE.MODE_ENCRYPT : mode;

		if (keyfile == null) keyfile = new File(inputfile.getAbsolutePath() + ".aeskey");

		logger.print("Starting process ...");
		logger.emptyln();

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
		logger.emptyln();

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
