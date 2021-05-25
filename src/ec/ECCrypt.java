package ec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import kmac.Sha3;
import util.DecryptionData;
import util.UtilMethods;

public class ECCrypt {
	
	/**
	 * Decrypt a file with a passphrase with Schnorr/ECDHIES scheme.
	 * 
	 * @param enc  encoded message byte array
	 * @param s    secret key
	 * @param outFile output file url
	 */
	public static String decryptToFile(byte[] enc, BigInteger s, String outFile) {
		// Decrypt data.
		DecryptionData dec = decrypt(enc, s);
		
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

//
//	public static String encryptFile(String inFile, String pass, String outFile) throws IOException {
//
//		outFile = outFile + ".ECcript";
//
//		// Read bytes from a file
//		byte[] msg = UtilMethods.readFileBytes(inFile);
//
//		// Convert passphrase to byte array
//		byte[] pw = (pass != null && pass.length() > 0) ? pass.getBytes() : new byte[0];
//
//		byte[] enc = encrypt(msg, pw);
//
//		String result = UtilMethods.writeBytesToFile(enc, outFile);
//
//		if (result.equals("")) {
//			return "Your file has been encrypted to " + outFile;
//		} else {
//			return result;
//		}
//	}

		/**
         * Decrypting a symmetric cryptogram under a secret key with
         * Schnorr/ECDHIES scheme.
         * @param enc cryptogram byte array
         * @param s secret key
         * @return DecryptionData holding ontaining the decrypted data and a validity
         *         flag.
         */
	private static DecryptionData decrypt(byte[] enc, BigInteger s) {
		// Separate (Z,c,t)
		ECPoint Z = ECPoint.toECPoint(Arrays.copyOfRange(enc, 0, ECPoint.BAL));
		byte[] c = Arrays.copyOfRange(enc, ECPoint.BAL, enc.length - 64);
		byte[] t = Arrays.copyOfRange(enc, enc.length - 64, enc.length);
		
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

	/**
	 * Encrypting a byte m under the Schorr/ECDHIES public key V.
	 * @param message Byte m.
	 * @param enc
	 * @param k
	 * @param V
	 * @return
	 * @throws IOException
	 */
	public static byte[] encrypt(byte[] message, byte[] enc, BigInteger k, ECPoint V) throws IOException {
		// k <-- Random(512)
		SecureRandom rand = new SecureRandom();
		byte[] key = new byte[64]; // 64 * 8 = 512
		rand.nextBytes(key);

		// W -> k*V; Z -> k*G
		ECPoint W = V.multiply(k);
		ECPoint Z = ECPoint.toECPoint(Arrays.copyOfRange(enc, 0, ECPoint.BAL)).multiply(k);

		// (ke || ka) = KMACXOF256(W_x, "", 1024, "P")
		byte[] ke_ka = Sha3.KMACXOF256(W.getX().toByteArray(), new byte[] {}, 1024, "P");

		byte[] ke = Arrays.copyOfRange(ke_ka, 0, 64);

		// c = KMACXOF256(ke, "", |m|, "PKE") XOR m
		byte[] c = UtilMethods.xorBytes(Sha3.KMACXOF256(ke, new byte[] {}, 8 * message.length, "PKE"), message);

		byte[] ka = Arrays.copyOfRange(ke_ka, 64, 128);

		// t = KMACXOF256(ka, m, 512, "PKA")
		byte[] t = Sha3.KMACXOF256(ka, message, 512, "PKA");



		// symmetric cryptogram: (Z, c, t)
		ByteArrayOutputStream symCryptogram = new ByteArrayOutputStream();
		symCryptogram.write(Z.toByteArray());
		symCryptogram.write(c);
		symCryptogram.write(t);
		return symCryptogram.toByteArray();
	}
}
