package main.me.thedome.aesencryptor.utils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * AES_Encryptor/PACKAGE_NAME
 * Created on 11/2016.
 */
public class ArgumentParser {


	private final ArrayList<String> args;
	private int index;


	public ArgumentParser(String[] args) {
		this.args = new ArrayList<>();
		Collections.addAll(this.args, args);
	}

	/**
	 * Get the next argument
	 *
	 * @return The next argument
	 */
	public String nextArgument() {
		return args.get(index++);
	}
}
