/**
 * This provides method to let elliptic-curve-related UI interact with 
 * back end (elliptic curve - ec), like an interface
 * @author Phong Le
 * @author Hung Vu
 * @author Duy Nguyen
 */
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
	 * @param enc     encoded message byte array
	 * @param s       secret key
	 * @param outFile output file url
	 */
	public static String decryptToFile(byte[] enc, BigInteger s, String outFile) {
		// Decrypt data.
		DecryptionData dec = decrypt(enc, s);
		if (dec == null) {
			return "Invalid message, please make sure the input is the data encrypted with EC Encrypt!";
		}

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
	 * Decrypting a symmetric cryptogram under a secret key with Schnorr/ECDHIES
	 * scheme.
	 * 
	 * @param enc cryptogram byte array
	 * @param s   secret key
	 * @return DecryptionData holding ontaining the decrypted data and a validity
	 *         flag.
	 */
	private static DecryptionData decrypt(byte[] enc, BigInteger s) {
		// Separate (Z,c,t)
		ECPoint Z = ECPoint.toECPoint(Arrays.copyOfRange(enc, 0, ECPoint.BAL));
		if (Z.getX() == null) {
			return null;
		}
		byte[] c = Arrays.copyOfRange(enc, ECPoint.BAL, enc.length - 64);
		byte[] t = Arrays.copyOfRange(enc, enc.length - 64, enc.length);

		// W <- s*Z
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
	 * Encrypt a file using elliptic curve cryptography
	 * 
	 * @param message an input message
	 * @param V       a public key
	 * @param outFile an output file
	 * @return a string displays result of an encryption process
	 * @throws IOException
	 */
	public static String encryptFile(byte[] message, ECPoint V, String outFile) throws IOException {

		outFile = outFile + ".ECcript";
		byte[] myEncrypt = encrypt(message, V);

		// Read bytes from a file
		String encryptResult = UtilMethods.writeBytesToFile(myEncrypt, outFile);

		// Convert passphrase to byte array
		if (encryptResult.equals(""))
			return "Your file has been encrypted to " + outFile;
		else
			return encryptResult;
	}

	/**
	 * Encrypting a byte m under the Schorr/ECDHIES public key V.
	 * 
	 * @param message Message byte array.
	 * @param V       Public Key V
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

		// V.multiply(k);
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

	/**
	 * Generating a signature for a byte array m under passphrase pw:
	 * 
	 * @param m          a byte array of a given file
	 * @param passphrase a given passphrase
	 * @return a signature
	 */
	private static ECSignature get_signature(byte[] m, String passphrase) {
		// s <- KMACXOF256(pw, "", 512, "K")
		byte[] s = Sha3.KMACXOF256(passphrase.getBytes(), "".getBytes(), 512, "K");
		// s <- 4s
		BigInteger sBigInteger = (new BigInteger(s)).multiply(BigInteger.valueOf(4L));
		// k <- KMACXOF256(s, m, 512, "N");
		byte[] k = Sha3.KMACXOF256(sBigInteger.toByteArray(), m, 512, "N");
		byte[] kBytes = new byte[65];
		System.arraycopy(k, 0, kBytes, 1, k.length); // assure k is positive
		// k <- 4k
		BigInteger kBigInteger = (new BigInteger(kBytes)).multiply(BigInteger.valueOf(4L));
		// U <- k*G
		ECPoint u = ECKeyPair.G.multiply(kBigInteger);
		// h <- KMACXOF256(U x , m, 512, "T");
		byte[] h = Sha3.KMACXOF256(u.getX().toByteArray(), m, 512, "T");
		byte[] hBytes = new byte[65];
		System.arraycopy(h, 0, hBytes, 1, h.length); // assure h is positive
		// z <- (k - hs) mod r
		BigInteger hBigInteger = new BigInteger(hBytes);
		BigInteger zBigInteger = (kBigInteger.subtract(hBigInteger.multiply(sBigInteger))).mod(ECPoint.R);
		return new ECSignature(hBigInteger, zBigInteger);
	}

	/**
	 * Verifying a signature (h, z) for a byte array m under the (Schnorr/ECDHIES)
	 * public key V
	 * 
	 * @param m         a given file
	 * @param signature a given signature
	 * @param v         a public key
	 * @return true if a signature is valid, false otherwise
	 */
	private static boolean verify_signature_result(byte[] m, ECSignature signature, ECPoint v) {
		// U <- z*G + h*V
		ECPoint u = (ECKeyPair.G.multiply(signature.get_z())) // z*G
				.add(v.multiply(signature.get_h())); // h*V
		// accept if, and only if, KMACXOF256(U x , m, 512, "T") = h
		byte[] temp = Sha3.KMACXOF256(u.getX().toByteArray(), m, 512, "T");
		byte[] tempBytes = new byte[65];
		System.arraycopy(temp, 0, tempBytes, 1, temp.length); // assuretemph is positive
		System.out.println(UtilMethods.bytesToHex(tempBytes));
		System.out.println(UtilMethods.bytesToHex(signature.get_h().toByteArray()));
		return signature.get_h().equals(new BigInteger(tempBytes)) ? true : false;
	}

	/**
	 * Read files and verify signature.
	 * 
	 * @param toVerifyPath  path of a file to be verified
	 * @param signaturePath path of a signature file
	 * @param publicKeyPath path of a public key file
	 * @return
	 */
	public static String verify_signature(String toVerifyPath, String signaturePath, String publicKeyPath) {
		byte[] m = UtilMethods.readFileBytes(toVerifyPath);
		byte[] signatureBytes = UtilMethods.readFileBytes(signaturePath);
		boolean isValid = true;
		ECSignature signature = ECSignature.toECSignature(signatureBytes);
		ECPoint v = ECKeyPair.readPubKeyFile(publicKeyPath);
		isValid = verify_signature_result(m, signature, v);

		return isValid ? "Signature is valid." : "Invalid signature";
	}

	/**
	 * Sign a file under the provided password, then writes it the same folder as
	 * input file.
	 * 
	 * @param pass          passphrase
	 * @param inputFilePath input file path
	 */
	public static String writeSignatureToFile(String pass, String inputFilePath) {
		// System.out.println(inputFilePath);
		String outputFolderPath = inputFilePath + ".sign";
		ECSignature signature = get_signature(UtilMethods.readFileBytes(inputFilePath), pass);
		String result = UtilMethods.writeBytesToFile(signature.toByteArray(), outputFolderPath);
		if (result.equals("")) {
			return "Signature file has been written to " + outputFolderPath;
		} else {
			return result;
		}
	}
}
