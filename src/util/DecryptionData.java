package util;

/**
 * Encapsulation of decrypted data and its validity.
 * 
 * @author Phong Hoang Le
 *
 */
public class DecryptionData {
	// Byte array of the decrypted data
	private byte[] decData;
	// A boolean indicating whether decryted data is valid
	private boolean isValid;

	/**
	 * Constructor of this encapsulation object
	 */
	public DecryptionData(byte[] decData, boolean isValid) {
		this.decData = decData;
		this.isValid = isValid;
	}

	/** Return the byte array of the decrypted data. */
	public byte[] getData() {
		return decData;
	}

	/** Return the validity status of the decrypted data. */
	public boolean isValid() {
		return isValid;
	}

}
