package src;

import java.math.BigInteger;

public class Encoder {
  public static void main (String[]aStrings){
    // 0001 0000 | 0011 0001 | 0111 1110
    String decimal = "1061246";
    byte[] result = left_encode(new BigInteger(decimal));
    for (int i = 0; i < result.length; i ++) {
      // Print byte as raw string: https://stackoverflow.com/questions/12310017/how-to-convert-a-byte-to-its-binary-string-representation
      String s1 = String.format("%8s", Integer.toBinaryString(result[i] & 0xFF)).replace(' ', '0');
      System.out.println("Reversed bits: " + s1);
    }
  }

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
    // 1. Let n be the smallest positive integer for which 2^8n > x.
    int n = (int) Math.ceil(Double.valueOf(x.bitLength()) / 8);
    // 2. Let x1, x2,…, xn be the base-256 encoding of x satisfying:
    //    x = sum(2^8(n-i)x_i), for i = 1 to n.
    // 3. Let Oi = enc8(xi), for i = 1 to n.
    // Side note: 2^2040 is around 615 digits => around 19680 bits
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
      // System.out.println("Reversed Int: " + reversedInt);
      xReversedArray[i] = (byte) reversedInt;
    }
    // 4. Let On+1 = enc8(n).
    xReversedArray[0] = (byte) Integer.reverse(n << 24);
    // 5. Return O = O_0 || O_1 || … || O_n−1 || O_n.
    return xReversedArray;
  }

}