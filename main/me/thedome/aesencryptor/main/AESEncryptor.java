package main.me.thedome.aesencryptor.main;

import main.me.thedome.aesencryptor.classes.DEBUG_MODE;
import main.me.thedome.aesencryptor.classes.OPERATION_MODE;
import main.me.thedome.aesencryptor.crypto.CryptoMethods;
import main.me.thedome.aesencryptor.utils.ArgumentParser;
import main.me.thedome.aesencryptor.utils.IOUtils;
import main.me.thedome.aesencryptor.utils.Logger;

import java.io.File;
import java.io.FileNotFoundException;

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
	public final int KEYSIZE = 256;
	private final CryptoMethods methods = new CryptoMethods();
	private final Logger logger = Logger.getInstance();
	public IOUtils fileUtils = IOUtils.getInstance();

	public boolean percentage_mode = false;
	public DEBUG_MODE debMode;

	public OPERATION_MODE mode;

	private byte[][] key;
	private boolean keyfile_exists;

	private AESEncryptor(String[] args) throws FileNotFoundException {

		fileUtils.setAEC(this);
		ArgumentParser parser = new ArgumentParser(args, this);
		parser.parseArgs();

		logger.setDebug(debMode, percentage_mode);

		mode = mode == null ? OPERATION_MODE.MODE_ENCRYPT : mode;

		if (fileUtils.keyfile == null) fileUtils.keyfile = new File(fileUtils.inputfile.getAbsolutePath() + ".aeskey");

		logger.print("Starting process ...");
		logger.emptyln();

		if (mode == OPERATION_MODE.MODE_ENCRYPT) {
			logger.debugln("Selected mode: encryption");

			if (fileUtils.outputfile == null) {
				logger.debug("Found no file to output. Writing to: " + fileUtils.inputfile.getAbsolutePath() + ".enc", DEBUG_MODE.MODE_NORMAL);
				fileUtils.outputfile = new File(fileUtils.inputfile.getAbsolutePath() + ".enc");
			}
		} else {
			logger.debugln("Selected mode: decryption");

			if (fileUtils.outputfile == null) {
				fileUtils.outputfile = new File(fileUtils.inputfile.getAbsolutePath().substring(0, fileUtils.inputfile.getAbsolutePath().length() - 4));
			}
		}
		logger.debugln("With keyfile: " + fileUtils.keyfile.getAbsolutePath());
		logger.print("With input File: " + fileUtils.inputfile.getAbsolutePath());
		logger.print("To: " + fileUtils.outputfile.getAbsolutePath());
		logger.emptyln();

		if (mode == OPERATION_MODE.MODE_ENCRYPT) {

			// If the keyfile exists
			if (fileUtils.keyfile.exists()) {
				// Read the key in ...
				readKey(fileUtils.keyfile);
				// Tell the system, that we use one ...
				keyfile_exists = true;
				logger.debugln("Found preexisting key at: " + fileUtils.keyfile.getAbsolutePath());
				logger.print("Found existing key. Using it!");
			} else { // else
				// Generate the key
				key = methods.genKey(KEYSIZE);
			}


			if (!keyfile_exists) fileUtils.writeKey(key);
			byte[] input = fileUtils.readInput(fileUtils.inputfile);
			methods.encrypt(input, key);

		} else { // Decryption mode
			// Read the key
			if (!fileUtils.keyfile.exists()) {
				throw new FileNotFoundException("The Keyfile specified cant be found");
			}
			// Read the key in
			readKey(fileUtils.keyfile);

			byte[] input = fileUtils.readInput();
			methods.decrypt(input, key);
		}

	}

	public static void main(String[] args) throws Exception {
		new AESEncryptor(args);
	}



	private void readKey(File keyfile) {
		logger.debugln("Reading key from: " + keyfile.getAbsolutePath());
		// the size iof every dimension
		int dimsize = (int) Math.sqrt(keyfile.length());

		byte[][] aes_key = new byte[dimsize][dimsize];
		byte[] keybytes = IOUtils.getInstance().readInput(keyfile);

		// The index
		int i = 0;

		for (int ax = 0; ax < aes_key.length; ax++){
			for(int ay = 0; ay < aes_key[ax].length; ay++){
				aes_key[ax][ay] = keybytes[i++];
			}
		}
		logger.debugln("Finished reading!");
		logger.debug("", false);

		this.key = aes_key;
	}

}
