package ec;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;

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

	public static String encryptFile(byte[] message, ECPoint V, String outFile) throws IOException {

		byte[] myEncrypt = encrypt(message, V);

		// Read bytes from a file
		String encryptResult = UtilMethods.writeBytesToFile(myEncrypt,outFile);

		// Convert passphrase to byte array
			return "Your file has been encrypted to " + outFile;
	}


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
	 * @param message Message byte array.
	 * @param V Public Key V
	 * @return The encrypted data.
	 * @throws IOException
	 */
	public static byte[] encrypt(byte[] message, ECPoint V) throws IOException {
		// k <-- Random(512)
		SecureRandom rand = new SecureRandom();
		byte[] key = new byte[64]; // 64 * 8 = 512
		rand.nextBytes(key);
		BigInteger k = new BigInteger(key);

		// W -> k*V; Z -> k*G
		ECPoint W = V.multiply(k);

		//V.multiply(k);
		ECPoint Z = ECKeyPair.G.multiply(k);

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

	// Generating a signature for a byte array m under passphrase pw:
	public static ECSignature get_signature (byte[] m, String passphrase) {
		// s <- KMACXOF256(pw, “”, 512, “K”)
		byte[] s = Sha3.KMACXOF256(passphrase.getBytes(), "".getBytes(), 512, "K");
		// s <- 4s
		BigInteger sBigInteger = (new BigInteger(s)).multiply(BigInteger.valueOf(4L));
		// k <- KMACXOF256(s, m, 512, “N”);
		byte[] k = Sha3.KMACXOF256(sBigInteger.toByteArray(), m, 512, "N");
		// k <- 4k
		BigInteger kBigInteger = (new BigInteger(k)).multiply(BigInteger.valueOf(4L));
		// U <- k*G
		ECPoint u = ECKeyPair.G.multiply(kBigInteger);
		// h <- KMACXOF256(U x , m, 512, “T”);
		byte[] h = Sha3.KMACXOF256(u.getX().toByteArray(), m, 512, "T");
		// z <- (k – hs) mod r
		BigInteger hBigInteger = new BigInteger(h);
		BigInteger zBigInteger = (kBigInteger.subtract(hBigInteger.multiply(sBigInteger))).mod(ECPoint.R);
		return new ECSignature(h, zBigInteger);
	}

	// Verifying a signature (h, z) for a byte array m under the (Schnorr/ECDHIES) public key V:
	public static boolean verify_signature (byte[] m, ECSignature signature, ECPoint v){
		// U <- z*G + h*V
		ECPoint u = (ECKeyPair.G.multiply(signature.get_z())) // z*G
			.add(v.multiply(new BigInteger(signature.get_h()))); // h*V
		// accept if, and only if, KMACXOF256(U x , m, 512, “T”) = h
		return Sha3.KMACXOF256(u.getX().toByteArray(), m, 512, "T").equals(signature.get_h()) ? true : false;
	}

}
