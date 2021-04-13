import java.math.BigInteger;

public class Keccak_bytepad {
   
   /**
     * Pads a byte string
     * @param X the input string to pad
     * @param w the desired factor of the padding
     * @return a byte array prepended by left_encode(w) such that it's length is a multiple of w
    */
    private static byte[] bytepad(byte[] X, int w) {
    	
        byte[] z = left_encode(BigInteger.valueOf(w));
        
        // Make sure the output byte string has length in bytes 
        // is a multiple of w
        int len = z.length + X.length;
        while ((len / 8 % w) != 0) {
           len += 1;
        }
        // Append the byte string to the encoded integer
        byte[] out = Arrays.copyOf(z, len);
        System.arraycopy(X, 0, out, z.length, X.length);

        return out;
    }

}