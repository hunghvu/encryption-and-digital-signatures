package kmac;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;

import util.DecryptionData;
import util.UtilMethods;


public class KCrypt {

	static void encryptFile(String inFile, String pass, String outfile) throws IOException {

		// Read bytes from a file
		byte[] enc = UtilMethods.readFileBytes(inFile);

		// Convert passphrase to byte array
		byte[] pw = (pass != null && pass.length() > 0) ? pass.getBytes() : new byte[0];

		try {
			FileOutputStream outputStream = new FileOutputStream(outfile + ".cryptogram");
			outputStream.write(encrypt(enc, pw));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Your file has been encrypted! Your file has been encrypted to " + UtilMethods.bytesToHex(enc));
	}

	private static byte[] encrypt(byte[] message, byte[] pwd) throws IOException {
         // z <-- Random(512)
        SecureRandom rand = new SecureRandom();
        byte[] z = new byte[64]; //64 * 8 = 512
        rand.nextBytes(z);


		// (ke || ka) = KMACXOF256(z || pw, “”, 1024, “S”)
        byte[] ke_ka = SHA3.KMACXOF256(UtilMethods.concat(z, pwd), new byte[]{}, 1024, "S");

        byte[] ke = Arrays.copyOfRange(ke_ka, 0, 64);
        // c = KMACXOF256(ke, “”, |m|, “SKE”) XOR m
		byte[] c = UtilMethods.xorBytes(SHA3.KMACXOF256(ke, new byte[]{}, 8* message.length, "SKE"), message);


        byte[] ka = Arrays.copyOfRange(ke_ka, 64, 128);
        // t = KMACXOF256(ka, m, 512, "SKA)
		byte[] t = SHA3.KMACXOF256(ka, message, 512, "SKA");

        //symmetric cryptogram: (z, c, t)
        ByteArrayOutputStream symCryptogram = new ByteArrayOutputStream();
        symCryptogram.write(z);
        symCryptogram.write(c);
        symCryptogram.write(t);
        return symCryptogram.toByteArray();
    }


	/**
	 * Decrypt a file with a passphrase.
	 * @param inFile input file url
	 * @param pass passphrase
	 * @param outFile output file url
	 */
	public static void decryptFile(String inFile, String pass, String outFile) {
		
		// Read byte array from file.
		byte[] enc = UtilMethods.readFileBytes(inFile);
		
		// Convert passphrase string to byte array.
		byte[] pw = (pass != null && pass.length() > 0) ? pass.getBytes() : new byte[0];
		
		// Decrypt with file and passphrase byte array.
		DecryptionData dec = decrypt(enc, pw);
		
		// Respond base on the validity of decrypted data.
		if (dec.isValid()) {
			UtilMethods.writeBytesToFile(dec.getData(), outFile);
			System.out.println("Decrypted data has been written to " + outFile);
		} else {
			System.out.println("Authentication is invalid. Decryption has failed.");
		}
	}
	
	/**
	 * Decrypting a symmetric cryptogram under a passphrase.
	 * @param enc cryptogram byte array
	 * @param pwd passphrase
	 * @return DecryptionData holding ontaining the decrypted data and a validity flag.
	 */
	private static DecryptionData decrypt(byte[] enc, byte[] pwd) {
		// Separate (z,c,t)
		byte[] z = Arrays.copyOfRange(enc, 0, 64);
        byte[] c = Arrays.copyOfRange(enc, 64, enc.length - 64);
        byte[] t = Arrays.copyOfRange(enc, enc.length - 64, enc.length);
        
        // (ke || ka) = KMACXOF256(z || pw, “”, 1024, “S”)
        byte[] ke_ka = SHA3.KMACXOF256(UtilMethods.concat(z,pwd), new byte[]{}, 1024, "S");
        
        // Separate (ke||ka)
        byte[] ke = Arrays.copyOfRange(ke_ka, 0, 64);
        byte[] ka = Arrays.copyOfRange(ke_ka, 64, 128);
        
        //m = KMACXOF256(ke, “”, |c|, “SKE”) XOR c
        byte[] m = UtilMethods.xorBytes(SHA3.KMACXOF256(ke, new byte[]{}, 8*c.length, "SKE"), c);
        
        //t’ = KMACXOF256(ka, m, 512, “SKA”)
        byte[] t_prime = SHA3.KMACXOF256(ka, m, 512, "SKA");
        
        return new DecryptionData(m, Arrays.equals(t, t_prime));               
	}
	
	

}
