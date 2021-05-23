package ec;

import java.math.BigInteger;
import java.util.Arrays;

import kmac.Sha3;
import util.DecryptionData;
import util.UtilMethods;

public class ECCrypt {
	
	/**
	 * Decrypt a file with a passphrase with Schnorr/ECDHIES scheme.
	 * 
	 * @param inFile  input file url
	 * @param pass    passphrase
	 * @param outFile output file url
	 */
	public static String decryptFile(String inFile, String pass, String outFile) {

		// Read byte array from file.
		byte[] enc = UtilMethods.readFileBytes(inFile);
		if (enc == null) return "Error occurred while reading file.";

		// Convert passphrase string to byte array.
		byte[] pw = (pass != null && pass.length() > 0) ? pass.getBytes() : new byte[0];

		// Decrypt with file and passphrase byte array.
		DecryptionData dec = decrypt(enc, pw);
		
		String result = UtilMethods.writeBytesToFile(enc, outFile);	
		if (result.equals("")) {
			// Respond base on the validity of decrypted data.
			if (dec.isValid()) {
				UtilMethods.writeBytesToFile(dec.getData(), outFile);
				return "Decrypted data has been written to " + outFile;
			} else {
				return "Authentication is invalid. Decryption has failed";
			}
		} else {
			return result;
		}
	}

	/**
	 * Decrypting a symmetric cryptogram under a passphrase with
	 * Schnorr/ECDHIES scheme.
	 * @param enc cryptogram byte array
	 * @param pwd passphrase
	 * @return DecryptionData holding ontaining the decrypted data and a validity
	 *         flag.
	 */
	private static DecryptionData decrypt(byte[] enc, byte[] pwd) {
		// Separate (Z,c,t)
		ECPoint Z = ECPoint.toECPoint(Arrays.copyOfRange(enc, 0, ECPoint.BAL));
		byte[] c = Arrays.copyOfRange(enc, ECPoint.BAL, enc.length - 64);
		byte[] t = Arrays.copyOfRange(enc, enc.length - 64, enc.length);
		
		//s <- KMACXOF256(pw, "", 512, "K"); s <- 4s
		byte[] outvalkey = Sha3.KMACXOF256(pwd, new byte[] {}, 512, "K");
		BigInteger s = new BigInteger(outvalkey).multiply(BigInteger.valueOf(4));
		
		//W <- s*Z
		ECPoint W = Z.multiply(s);
			
		// (ke || ka) = KMACXOF256(W_x, "", 1024, "P")
		byte[] ke_ka = Sha3.KMACXOF256(W.getX().toByteArray(), new byte[] {}, 1024, "P");

		// Separate (ke||ka)
		byte[] ke = Arrays.copyOfRange(ke_ka, 0, 64);
		byte[] ka = Arrays.copyOfRange(ke_ka, 64, 128);
		
		// m = KMACXOF256(ke, "", |c|, "PKE") XOR c
		byte[] m = UtilMethods.xorBytes(Sha3.KMACXOF256(ke, new byte[] {}, 8 * c.length, "PKE"), c);

		// t' = KMACXOF256(ka, m, 512, "PKA")
		byte[] t_prime = Sha3.KMACXOF256(ka, m, 512, "PKA");

		return new DecryptionData(m, Arrays.equals(t, t_prime));
	}	

}
