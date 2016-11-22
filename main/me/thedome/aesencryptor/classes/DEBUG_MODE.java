package main.me.thedome.aesencryptor.classes;

/**
 * AES_Encryptor/PACKAGE_NAME
 * Created on 11/2016.
 */
public enum DEBUG_MODE {

	MODE_SUPRESS(0), MODE_NORMAL(1), MODE_ALL(2);

	public final int level;

	DEBUG_MODE(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	/**
	 * Gets the mode by specifying the level
	 *
	 * @param level The Debug level
	 *
	 * @return null, if the level doesnt exist, other, the mode
	 */
	public DEBUG_MODE getByLevel(int level) {
		for (DEBUG_MODE m : DEBUG_MODE.values()) {
			if (m.level == level) return m;
		}

		return null;
	}

}
