package ec;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.util.Arrays;

import jdk.jshell.execution.Util;
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

	// Generating a signature for a byte array m under passphrase pw:
	private static ECSignature get_signature(byte[] m, String passphrase) {
		// s <- KMACXOF256(pw, "", 512, "K")
		byte[] s = Sha3.KMACXOF256(passphrase.getBytes(), "".getBytes(), 512, "K");
		// s <- 4s
		BigInteger sBigInteger = (new BigInteger(s)).multiply(BigInteger.valueOf(4L));
		// k <- KMACXOF256(s, m, 512, "N");
		byte[] k = Sha3.KMACXOF256(sBigInteger.toByteArray(), m, 512, "N");
		// k <- 4k
		BigInteger kBigInteger = (new BigInteger(k)).multiply(BigInteger.valueOf(4L));
		// U <- k*G
		ECPoint u = ECKeyPair.G.multiply(kBigInteger);
		// h <- KMACXOF256(U x , m, 512, "T");
		byte[] h = Sha3.KMACXOF256(u.getX().toByteArray(), m, 512, "T");
		// z <- (k - hs) mod r
		BigInteger hBigInteger = new BigInteger(h);
		BigInteger zBigInteger = (kBigInteger.subtract(hBigInteger.multiply(sBigInteger))).mod(ECPoint.R);
		return new ECSignature(h, zBigInteger);
	}

	// Verifying a signature (h, z) for a byte array m under the (Schnorr/ECDHIES)
	// public key V:
	private static boolean verify_signature_result(byte[] m, ECSignature signature, ECPoint v) {
		// U <- z*G + h*V
		ECPoint u = (ECKeyPair.G.multiply(signature.get_z())) // z*G
				.add(v.multiply(new BigInteger(signature.get_h()))); // h*V
		// accept if, and only if, KMACXOF256(U x , m, 512, "T") = h
		return Sha3.KMACXOF256(u.getX().toByteArray(), m, 512, "T").equals(signature.get_h()) ? true : false;
	}
	

	/**
	 * Read files and verify signature.
	 * 
	 * @param toVerifyPath path of a file to be verified
	 * @param signaturePath path of a signature file
	 * @param publicKeyPath path of a public key file
	 * @return
	 */
	public static String verify_signature(String toVerifyPath, String signaturePath, String publicKeyPath) {
		byte[] m = UtilMethods.readFileBytes(toVerifyPath);
		byte[] signatureBytes = UtilMethods.readFileBytes(signaturePath);
		byte[] publicKeyBytes = UtilMethods.readFileBytes(publicKeyPath);
		boolean isValid = true;
		try {
			ObjectInputStream signatureStream = new ObjectInputStream(new ByteArrayInputStream(signatureBytes));
			ECSignature signature = (ECSignature) signatureStream.readObject();

			ObjectInputStream publicKeyStream = new ObjectInputStream(new ByteArrayInputStream(publicKeyBytes));
			ECKeyPair publicKey = (ECKeyPair) publicKeyStream.readObject();
			ECPoint v = publicKey.getPublicKey();
			isValid = verify_signature_result(m, signature, v);

			signatureStream.close();
			publicKeyStream.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return isValid ? "Signature is valid." : "Invalid signature";
	}
	/**
	 * Sign a file under the provided password, then writes it the same folder as input file.
	 * 
	 * @param pass passphrase
	 * @param inputFilePath input file path
	 */
	public static String writeSignatureToFile(String pass, String inputFilePath) {
		// System.out.println(inputFilePath);
		String outputFolderPath = inputFilePath + ".sign";
		ECSignature signature = ECCrypt.get_signature(UtilMethods.readFileBytes(inputFilePath), pass);
		byte[] signatureBytes = UtilMethods.concat(signature.get_h(), signature.get_z().toByteArray());
		String result = UtilMethods.writeBytesToFile(signatureBytes, outputFolderPath);
		if (result.equals("")) {
			return "Signature file has been written to " + inputFilePath;
		} else {
			return result;
		}
	}
}
