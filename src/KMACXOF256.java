
import java.math.BigInteger;
import java.util.Arrays;

public class KMACXOF256 {
	/**
	   * left_encode(x) encodes the integer x as a byte string in a way that can be unambiguously parsed
	   * from the beginning of the string by inserting the length of the byte string before the byte string
	   * representation of x.
	   * 
	   * Validity Conditions: 0 <= x < 2^2040
	   * 
	   * More explanation: 
	   * https://crypto.stackexchange.com/questions/75269/sha3-the-left-right-encode-functions
	   * https://cryptologie.net/article/388/shake-cshake-and-some-more-bit-ordering/
	   * 
	   * @param x an arbitrary large integer with validity Conditions: 0 <= x < 2^2040
	   */
	  private static byte[] left_encode(BigInteger x){
	    // 1. Let n be the smallest positive integer for which 2^8n > x
	    int n = (int) Math.ceil(Double.valueOf(x.bitLength()) / 8);
	    // 2. Let x_1, x_2,…, x_n be the base-256 encoding of x satisfying:
	    //    x = sum(2^8(n-i) * x_i), for i = 1 to n
	    // 3. Let O_i = enc8(xi), for i = 1 to n
	    // Side note: 2^2040 is around 615 digits => around 19680 bits (enough for byte array)
	    byte[] xArray = x.toByteArray();
	    byte[] xReversedArray = new byte[xArray.length + 1];
	    int j = 1; // First slot saved for reversed-length-byte
	    for (int i = xArray.length - 1; i >= 0; i--) {
	      xReversedArray[j] = xArray[i];
	      j ++;
	    }
	    // Reverse bits
	    for (int i = 1; i < xReversedArray.length; i++){
	      int reversedInt = Integer.reverse((int) xReversedArray[i] << 24); // int is 4 bytes, so shift 24 after reversal
	      xReversedArray[i] = (byte) reversedInt;
	    }
	    // 4. Let O_0 = enc_8(n)
	    xReversedArray[0] = (byte) Integer.reverse(n << 24);
	    // 5. Return O = O_0 || O_1 || … || O_n−1 || O_n
	    return xReversedArray;
	  }

	  /**
	   * right_encode(x) encodes the integer x as a byte string in a way that can be unambiguously parsed
	   * from the end of the string by inserting the length of the byte string after the byte string
	  *  representation of x.
	   * 
	   * Validity Conditions: 0 <= x < 2^2040
	   * 
	   * More explanation: 
	   * https://crypto.stackexchange.com/questions/75269/sha3-the-left-right-encode-functions
	   * https://cryptologie.net/article/388/shake-cshake-and-some-more-bit-ordering/
	   * 
	   * @param x an arbitrary large integer with validity Conditions: 0 <= x < 2^2040
	   */
	  private static byte[] right_encode(BigInteger x){
	    // 1. Let n be the smallest positive integer for which 2^8n > x
	    int n = (int) Math.ceil(Double.valueOf(x.bitLength()) / 8);
	    // 2. Let x_1, x_2,…, x_n be the base-256 encoding of x satisfying:
	    //    x = sum(2^8(n-i) * x_i), for i = 1 to n
	    // 3. Let O_i = enc8(xi), for i = 1 to n
	    // Side note: 2^2040 is around 615 digits => around 19680 bits (enough for byte array)
	    byte[] xArray = x.toByteArray();
	    byte[] xReversedArray = new byte[xArray.length + 1];
	    int j = 0;
	    for (int i = xArray.length - 1; i >= 0; i--) {
	      xReversedArray[j] = xArray[i];
	      j ++;
	    }
	    // Reverse bits
	    for (int i = 1; i < xReversedArray.length; i++){
	      int reversedInt = Integer.reverse((int) xReversedArray[i] << 24); // int is 4 bytes, so shift 24 after reversal
	      xReversedArray[i] = (byte) reversedInt;
	    }
	    // 4. Let O_n+1 = enc_8(n)
	    xReversedArray[xArray.length] = (byte) Integer.reverse(n << 24);
	    // 5. Return O = O_1 || O_2 || … || O_n || O_n+1
	    return xReversedArray;
	  }
	  
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
	    while ((len % w) != 0) {
	       len += 1;
	    }
	    // Append the byte string to the encoded integer
	    byte[] out = Arrays.copyOf(z, len);
	    System.arraycopy(X, 0, out, z.length, X.length);
	
	    return out;
	}
	

//	private static byte[] encode_string(byte[] S) {
//	    //	Validity Conditions: 0 ≤ len(S) < 2^2040
////	    int slen = (S != null) ? S.length : 0;
////	    byte[] lenS = (S != null) ? left_encode(slen << 3) : left_encode_0; // NB: bitlength, not bytelength
////	    byte[] encS = new byte[lenS.length + slen];
////	    System.arraycopy(lenS, 0, encS, 0, lenS.length);
////	    System.arraycopy((S != null) ? S : encS, 0, encS, lenS.length, slen);
////	    return encS; // left_encode(len(S)) || S.
//	}
//        public static byte[] KMACOF256(byte[] X, int L, byte[] K, byte[] S) {
//        // Validity Conditions: len(N) < 2^2040 and len(S) < 2^2040
//
//            byte[] val = bytepad(concat(encode_string(K),136) ,right_encode(0)) ;
//            //update(val, val.length);
//
//        return cShake256(val,L,"KMAC",S);   
//    }
}

