import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

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
	// Debug mode?
	private boolean debug = false;
	private boolean percentage_mode = false;
	private operation_mode mode;
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

		mode = mode == null ? operation_mode.MODE_ENCRYPT : mode;

		if (keyfile == null) keyfile = new File(inputfile.getAbsolutePath() + ".aeskey");

		print("Starting process ...");
		if (mode == operation_mode.MODE_ENCRYPT) {
			debugln("Selected mode: encryption");

			if (outputfile == null) {
				outputfile = new File(inputfile.getAbsolutePath() + ".enc");
			}
		} else {
			debugln("Selected mode: decryption");

			if (outputfile == null) {
				outputfile = new File(inputfile.getAbsolutePath().substring(0, inputfile.getAbsolutePath().length() - 4));
			}
		}
		debugln("With keyfile: " + keyfile.getAbsolutePath());
		print("With input File: " + inputfile.getAbsolutePath());
		print("To: " + outputfile.getAbsolutePath());

		if(mode == operation_mode.MODE_ENCRYPT){

			// If the keyfile exists
			if(keyfile.exists()){
				// Read the key in ...
				readKey(keyfile);
				// Tell the system, that we use one ...
				keyfile_exists = true;
				debugln("Found preexisting key");
			} else { // else
				// Generate the key
				genKey(KEYSIZE);
			}


			if (!keyfile_exists) writeKey(key);
			byte[] input = readInput(inputfile);
			byte[] encrypted = encrypt(input, key);
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
			byte[] encrypted = decrypt(input, key);
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
					debug = true;
					break;
				case "-k":
					keyfile = new File(args[i + 1]);
					break;
				case "-e":
					mode = operation_mode.MODE_ENCRYPT;
					break;
				case "-d":
					mode = operation_mode.MODE_DECRYPT;
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
		int dimsize = (int) keyfile.length();

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
		print("Saving Key ...");

		// The index
		int i = 0;

		for(byte[] b : key){
			for (byte b1 : b){
				out[i++] = b1;
			}
		}

		debugln("Writing key to File: " + keyfile.getAbsolutePath());
		writeOutput(out, keyfile);

	}

	private void debugln(String text) {
		if (debug)
			System.out.println("[DEBUG] " + text);
	}

	private void debug(String text) {
		if (debug)
			System.out.print("[DEBUG] " + text);
	}

	private void print(String text) {
		System.out.println(text);
	}

	/**
	 * Encrypt the byte[]
	 * @param in The bytes to be encrypted
	 * @param key The key used for encryption
	 * @return The encrypted bytes
	 */
	private byte[] encrypt(byte[] in, byte[][] key) {
		byte[] encrypted = new byte[in.length];

		// Notify the user
		print("Starting encryption ...");

		byte upper;
		byte lower;
		// The index
		int i = 0;
		// The value of the encrypted
		double percent;

		System.out.println("");
		debugln("Encrypting: ...");
		// All the bytes in the array will be encrypted
		for(byte b : in){

			for(int i1 =0; i1 < 60; i1++) System.out.print("\b");


			// The upper 4 bits of the byte
			// 0100 0110 -> 0100 0000 -> 0000 0100
			upper = (byte) ((b & 0xF0) >> 4);

			// The lower 4 bits of the byte
			// 0100 0110 -> 0000 0110
			lower = (byte) (b & 0x0F);

			// Swap the with the key bytes
			encrypted[i++] = key[upper][lower];

			// Compute the percentage rate of the process
			percent = (double) Math.round(((double) i / (double) in.length) * 10000) / 100;


			percentln(percent);

		}

		if(debug) System.out.println("");
		print("Finished!");
		System.out.println("");

		return encrypted;
	}

	/**
	 * Method to decrypt all with the key
	 * @param in The Input bytes
	 * @param key The Key used to encrypt
	 * @return The decrypted bytes
	 */
	private byte[] decrypt(byte[] in, byte[][] key) {

		print("Starting decryption ...");
		// The output
		byte[] output = new byte[in.length];

		// The index o the loop
		int i = 0;
		// The upper 4 bits
		byte upper;
		// The lower 4 bits
		byte lower;
		// The percent
		double percent;

		// Should we exit?
		boolean exit;

		debugln("Decrypting " + in.length + " Bytes ...");
		System.out.println("");
		debugln("Decrypting: ");

		for (byte b : in){
			exit = false;
			for(int x = 0; x < key.length; x++){
				for(int y = 0; y < key[x].length; y++){
					if(b == key[x][y]){
						// Exit the next loop
						exit = true;

						// We found the equivalent bytes in the grid....
						upper = (byte) (x << 4);
						lower = (byte) y;

						output[i++] = (byte) (upper | lower);

						percent = ((double) Math.round(((double) i / (double) in.length) * 10000)) / 100;


						// Print the percent to the debug console
						percentln(percent);

						break;

					}
				}

				// We found, what we needed ... exit
				if(exit)break;
			}

		}

		if(debug) System.out.println("");
		print("Finished ...");

		return output;
	}

	private void percentln(double percent) {

		if (!debug || !percentage_mode) return;

		debug("\r");

		String hashtags = "";
		for(int i = 0; i < (percent / 10); i++){
			hashtags += "#";
		}
		while(hashtags.length() < 10){
			hashtags += " ";
		}

		if (percentage_mode) {
			System.out.print("[" + hashtags + "] " + percent + "% of process complete ...");
		} else {
			debug("[" + hashtags + "] " + percent + "% of process complete ...");
		}


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

		// The path for the File
		Path p = Paths.get(output.getAbsolutePath());
		try {
			Files.write(p, bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Generate the key for the encryption
	 * @param keysize The size of the key. Please do not edit. Some fixes needed
	 */
	private void genKey(int keysize) {

		print("Generating " + keysize + " Bit key ...");
		// the size iof every dimension
		int dimsize = (int) Math.sqrt(KEYSIZE);
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

		System.out.println("");
		debugln("Generating key: ");

		for(int x = 0; x < dimsize; x++){
			for(int y = 0; y < dimsize; y++){

				// next run ...
				unique = true;

				debug("\r"); // Erase line content

				// Get every single content
				byte[] byte1 = new byte[1];

				while(unique){
					rnd.nextBytes(byte1);

					unique = keybytes.contains(byte1[0]);
					keybytes.add(byte1[0]);
				}

				key[x][y] = byte1[0];

				percent = ((double) Math.round(((double) i++ / (double) KEYSIZE) * 10000) / 100);


				// Print the percent to the debug console
				percentln(percent);


			}
			}


		this.key = key;

	}

	private enum operation_mode {
		MODE_ENCRYPT, MODE_DECRYPT
	}
}
