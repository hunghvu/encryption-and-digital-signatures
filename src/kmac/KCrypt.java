package kmac;


import util.DecryptionData;
import util.UtilMethods;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;

import ec.ECPoint;

public class KCrypt {

	public static String encryptFile(String inFile, String pass, String outFile) throws IOException {
		
		outFile = outFile + ".cript";
		
		// Read bytes from a file
		byte[] msg = UtilMethods.readFileBytes(inFile);

		// Convert passphrase to byte array
		byte[] pw = (pass != null && pass.length() > 0) ? pass.getBytes() : new byte[0];
		
		byte[] enc = encrypt(msg, pw);

		String result = UtilMethods.writeBytesToFile(enc, outFile);
		
		if (result.equals("")) {
			return "Your file has been encrypted to " + outFile;
		} else {
			return result;
		}	
	}

	public static byte[] encrypt(byte[] message, byte[] pwd) throws IOException {
		// z <-- Random(512)
		SecureRandom rand = new SecureRandom();
		byte[] z = new byte[64]; // 64 * 8 = 512
		rand.nextBytes(z);

		// (ke || ka) = KMACXOF256(z || pw, "", 1024, "S")
		byte[] ke_ka = Sha3.KMACXOF256(UtilMethods.concat(z, pwd), new byte[] {}, 1024, "S");

		byte[] ke = Arrays.copyOfRange(ke_ka, 0, 64);
		// c = KMACXOF256(ke, "", |m|, "SKE") XOR m
		byte[] c = UtilMethods.xorBytes(Sha3.KMACXOF256(ke, new byte[] {}, 8 * message.length, "SKE"), message);

		byte[] ka = Arrays.copyOfRange(ke_ka, 64, 128);
		// t = KMACXOF256(ka, m, 512, "SKA)
		byte[] t = Sha3.KMACXOF256(ka, message, 512, "SKA");

		// symmetric cryptogram: (z, c, t)
		ByteArrayOutputStream symCryptogram = new ByteArrayOutputStream();
		symCryptogram.write(z);
		symCryptogram.write(c);
		symCryptogram.write(t);
		return symCryptogram.toByteArray();
	}

	/**
	 * KMAC Decrypt a file with a passphrase.
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
	 * Decrypting a symmetric cryptogram under a passphrase.
	 * 
	 * @param enc cryptogram byte array
	 * @param pwd passphrase
	 * @return DecryptionData holding ontaining the decrypted data and a validity
	 *         flag.
	 */
	public static DecryptionData decrypt(byte[] enc, byte[] pwd) {
		// Separate (z,c,t)
		byte[] z = Arrays.copyOfRange(enc, 0, 64);
		byte[] c = Arrays.copyOfRange(enc, 64, enc.length - 64);
		byte[] t = Arrays.copyOfRange(enc, enc.length - 64, enc.length);

		// (ke || ka) = KMACXOF256(z || pw, "", 1024, "S")
		byte[] ke_ka = Sha3.KMACXOF256(UtilMethods.concat(z, pwd), new byte[] {}, 1024, "S");

		// Separate (ke||ka)
		byte[] ke = Arrays.copyOfRange(ke_ka, 0, 64);
		byte[] ka = Arrays.copyOfRange(ke_ka, 64, 128);

		// m = KMACXOF256(ke, "", |c|, "SKE") XOR c
		byte[] m = UtilMethods.xorBytes(Sha3.KMACXOF256(ke, new byte[] {}, 8 * c.length, "SKE"), c);

		// t" = KMACXOF256(ka, m, 512, "SKA")
		byte[] t_prime = Sha3.KMACXOF256(ka, m, 512, "SKA");

		return new DecryptionData(m, Arrays.equals(t, t_prime));
	}

	// Return SHA3 in hex string of a file
	public static String get_sha3_file(File directory) {
		StringBuilder sb = new StringBuilder();
		try {
			byte[] data = Files.readAllBytes(directory.toPath());
			int length = 512;
			String s = "D";
			// Must use "".getBbytes(), not an empty key byte array.
			byte[] outval = Sha3.KMACXOF256("".getBytes(), data, length, s);
			sb.append(UtilMethods.bytesToHex(outval));

		} catch (IOException e) {
			System.out.println("get_sha3_file IOException");
		}
		return sb.toString();
	}

	// Return SHA3 in hex string of a given text input
	public static String get_sha3_text(String m) {
		byte[] data = m.getBytes();
		int length = 512;
		String s = "D";
		byte[] outval = Sha3.KMACXOF256("".getBytes(), data, length, s);
		return UtilMethods.bytesToHex(outval);
	}

	// Return MAC of a given file in hex string
	public static String get_mac_file(File directory, String passphrase) {
		StringBuilder sb = new StringBuilder();
		try {
			byte[] data = Files.readAllBytes(directory.toPath());
			int length = 512;
			String s = "T";
			// Must use "".getBbytes(), not an empty key byte array.
			byte[] outval = Sha3.KMACXOF256(passphrase.getBytes(), data, length, s);
			sb.append(UtilMethods.bytesToHex(outval));

		} catch (IOException e) {
			System.out.println("get_sha3_file IOException");
		}
		return sb.toString();
	}

}
