package ec;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

import kmac.KCrypt;
import kmac.Sha3;
import util.DecryptionData;
import util.UtilMethods;

public class ECKeyPair {
	
	/** The static point of the public key. */
    public static final ECPoint G = new ECPoint(BigInteger.valueOf(4L), false);
    /** The public key, generated based on the provided passphrase. */
    private final ECPoint V;
    /** The secret key. */
    private final BigInteger s;
	
    /**
     * Generating a (Schnorr/ECDHIES) key pair from passphrase pw
     * @param passphrase the provided passphrase
     */
	public ECKeyPair(String passphrase) {
		byte[] outvalkey = Sha3.KMACXOF256(passphrase.getBytes(), new byte[] {}, 512, "K");
		// s <- KMACXOF256(pw, "", 512, "K"); s <- 4s. (s) in this case is sKey
		s = (new BigInteger(outvalkey)).multiply(BigInteger.valueOf(4));
		// V <- s*G
		V = G.multiply(s);
	}
	
	/**
     * Constructs a new key pair with the provided private key. 
     * @param s the private key 
     */
    public ECKeyPair(BigInteger s) {
        this.s = s;
        V = G.multiply(s);
    }

	
	/**
	 * Get the public key.
	 */
	public ECPoint getPublicKey() {
		return V;
	}
	
	/**
	 * Get the private key.
	 */
	public BigInteger getPrivateKey() {
		return s;
	}
	
	/**
     * Encrypts the private key under the provided password, then
     * writes it to the path provided.
     * @param url the desired file name
     */
    public String encPrvToFile(byte[] pass, String path) {
        try {
			UtilMethods.writeBytesToFile(KCrypt.encrypt(s.toByteArray(), pass), path);
			return "Encrypted data has been written to " + path;
		} catch (IOException e) {
			return "Error occurred while writing output to file.";
		}
    }
	
	/**
     * Reads the specified private key file and returns a new ECKeyPair object
     * @param path the path to the file containing the serialized private key
     * @param pwd the password used to write the secret key to the file
     * @return a new ECKeyPair object containing the private key of the file
     */
    public static ECKeyPair readPrivateKeyFile(String path, String pwd) {
    	byte[] enc = UtilMethods.readFileBytes(path);
    	if (enc == null) {
    		return null;
    	}
        DecryptionData prvBytes = KCrypt.decrypt(enc, pwd.getBytes());
        if (!prvBytes.isValid()) {
            return null;
        }
        return new ECKeyPair(new BigInteger(prvBytes.getData()));
    }



}
