package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Provide utility methods
 * @author Phong Hoang Le
 *
 */
public class UtilMethods {
	
	/************************************************************
	 *                       Bytes Array Stuff				    *
	 ************************************************************/
	
	/**
	 * Concatenation function for 2 byte arrays. Assist from https://stackoverflow.com/questions/5513152/easy-way-to-concatenate-two-byte-arrays
	 * @param a Byte Array a
	 * @param b Byte Array b
	 * @return A concatenation of a and b as c.
	 */
	public static byte[] concat(byte[] a, byte[]b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
	  
	/**
     * XOR two byte arrays.
     * @param b1 the first array
     * @param b2 the second array
     * @return XORed byte arrays from b1 and b2
     */
    public static byte[] xorBytes(byte[] b1, byte[] b2) {
        if (b1.length != b2.length) throw new IllegalArgumentException("Input arrays are of different lengths");
        byte[] out = new byte[b1.length];
        for (int i = 0; i < b1.length; i++) {
            out[i] = (byte) (b1[i] ^ b2[i]);
        }
        return out;
    }
    

    
	/************************************************************
	 *                       Files Stuff				        *
	 ************************************************************/
    
    /**
     * Read byte arrays from a file.
     * @param filepath file path
     * @return byte arrays reading of the file
     */
    public static byte[] readFileBytes(String filePath) {
        byte[] out = null;
        try {
            FileInputStream fileIn = new FileInputStream(filePath);
            out = fileIn.readAllBytes();
            fileIn.close();
        } catch (FileNotFoundException fne) {
            System.out.println("Unable to locate file from path: " + filePath + ", is the URL correct?");
        } catch (IOException iox) {
            System.out.println("Error occurred while reading file: ." + filePath);
            iox.printStackTrace();
        }
        
        return out;
    }
    
    /**
     * Write byte arrays into a file.
     * @param toWrite byte arrays used to write to file
     * @param filePath path of destination file
     */
    public static String writeBytesToFile(byte[] toWrite, String filePath) {
        try {
            FileOutputStream outFile = new FileOutputStream(filePath);
            outFile.write(toWrite);
            outFile.close();
        } catch (FileNotFoundException e) {
            return "Unable to locate file from path: " + filePath + ", is the URL correct?";
        } catch (IOException iox) {
            return "Error occurred while writing output to file.";
        }
        return "";
    }
    
    
    /************************************************************
	 *                       Hex Stuff 				            *
	 ************************************************************/
    
    /** An entity used for bytesToHex method. */
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    
    /**
     * Covert a byte array to a hex string.
     * Code taken from https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java.
     * @param bytes input byte array
     * @return hex representation of the byte array
     */
    public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
    }
	

}
