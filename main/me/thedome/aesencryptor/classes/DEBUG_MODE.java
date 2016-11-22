package main.me.thedome.aesencryptor.classes;

/**
 * AES_Encryptor/PACKAGE_NAME
 * Created on 11/2016.
 */
public enum DEBUG_MODE {

	MODE_NORMAL(1), MODE_SUPRESS(0), MODE_ALL(2);

	public final int level;

	DEBUG_MODE(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

}
