/**
 * AES_Encryptor/PACKAGE_NAME
 * Created on 11/2016.
 */
public enum KEY_SIZE {

	KEYSIZE_256(256), KEYSIZE_1024(1024), KEYSIZE_4096(4096);

	int bits;

	KEY_SIZE(int bits) {
		this.bits = bits;
	}

	public int getByteSize() {
		return this.bits;
	}
}
