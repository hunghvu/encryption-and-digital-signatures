package ec;

import java.math.BigInteger;
import java.util.HashMap;

import kmac.Sha3;

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
		s = new BigInteger(outvalkey);
		
		// s <- KMACXOF256(pw, "", 512, "K"); s <- 4s. (s) in this case is sKey
		BigInteger s_t4 = s.multiply(BigInteger.valueOf(4));		
		// V <- s*G
		V = G.multiply(s_t4);
	}
	
	/**
	 * Get the public key.
	 */
	public ECPoint getPublicKey() {
		return V;
	}
	
	public BigInteger getSecretKey() {
		return s;
	}


}
