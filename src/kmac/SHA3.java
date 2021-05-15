package kmac;

import java.math.BigInteger;

import java.util.Arrays;
import util.UtilMethods;

/**
 * SHA3 implementation, adapted from version created by: 
 * Markku-Juhani O. Saarinen <mjos@iki.fi> - https://github.com/mjosaarinen/tiny_sha3
 * Paulo Barreto - Code preseted during office hour
 */
public class SHA3 {

  public byte[] st = new byte[200]; // st->q
  public int pt, rsiz, mdlen;

  // constants
  private static final int KECCAKF_ROUNDS = 24;

  private static final long[] keccakf_rndc = { 0x0000000000000001L, 0x0000000000008082L, 0x800000000000808aL,
      0x8000000080008000L, 0x000000000000808bL, 0x0000000080000001L, 0x8000000080008081L, 0x8000000000008009L,
      0x000000000000008aL, 0x0000000000000088L, 0x0000000080008009L, 0x000000008000000aL, 0x000000008000808bL,
      0x800000000000008bL, 0x8000000000008089L, 0x8000000000008003L, 0x8000000000008002L, 0x8000000000000080L,
      0x000000000000800aL, 0x800000008000000aL, 0x8000000080008081L, 0x8000000000008080L, 0x0000000080000001L,
      0x8000000080008008L };
  private static final int[] keccakf_rotc = { 1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 2, 14, 27, 41, 56, 8, 25, 43, 62, 18,
      39, 61, 20, 44 };
  private static final int[] keccakf_piln = { 10, 7, 11, 17, 18, 3, 5, 16, 8, 21, 24, 4, 15, 23, 19, 13, 12, 2, 20, 14,
      22, 9, 6, 1 };
  
  private static final byte[] right_encode_0 = {(byte)0x00, (byte)0x01}; // right_encode(0)
  private static final byte[] left_encode_0 = {(byte)0x01, (byte)0x00}; // left_encode(0)


  public SHA3() {

  }

  /**
   * Rotate the 64-bit long value x by y positions to the left Adapted from
   * https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.h
   * 
   * @param x any 64-bit long value
   * @param y the left-rotation displacement
   * @return the 64-bit long value x left-rotated by y positions
   */
  private static long ROTL64(long x, int y) {
    return (x << y) | (x >>> (64 - y));
  }

  /**
   * The Keccack-p permutation, ref section 3.3 NIST FIPS 202. Adapted from
   * https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
   * 
   * @param stateIn the input state array
   * @return the state after the Keccak-p permutation has been applied
   */
  private void sha3_keccakf(byte[/* 200 */] v) {
    // variables
    long t;
    long[] q = new long[25];
    long[] bc = new long[5];

    // Map from byte[] to long[]
    // j+=8 to go to next bunch of long
    for (int i = 0, j = 0; i < 25; i++, j += 8) {
      q[i] = (((long) v[j + 0] & 0xFFL)) | (((long) v[j + 1] & 0xFFL) << 8) | (((long) v[j + 2] & 0xFFL) << 16)
          | (((long) v[j + 3] & 0xFFL) << 24) | (((long) v[j + 4] & 0xFFL) << 32) | (((long) v[j + 5] & 0xFFL) << 40)
          | (((long) v[j + 6] & 0xFFL) << 48) | (((long) v[j + 7] & 0xFFL) << 56);
    }

    // actual iteration
    for (int r = 0; r < KECCAKF_ROUNDS; r++) {
      // Theta
      for (int i = 0; i < 5; i++)
        bc[i] = q[i] ^ q[i + 5] ^ q[i + 10] ^ q[i + 15] ^ q[i + 20];

      for (int i = 0; i < 5; i++) {
        t = bc[(i + 4) % 5] ^ ROTL64(bc[(i + 1) % 5], 1);
        for (int j = 0; j < 25; j += 5)
          q[j + i] ^= t;
      }

      // Rho Pi
      t = q[1];
      for (int i = 0; i < 24; i++) {
        int j = keccakf_piln[i];
        bc[0] = q[j];
        q[j] = ROTL64(t, keccakf_rotc[i]);
        t = bc[0];
      }

      // Chi
      for (int j = 0; j < 25; j += 5) {
        for (int i = 0; i < 5; i++) {
          bc[i] = q[j + i];
        }
        for (int i = 0; i < 5; i++) {
          q[j + i] ^= (~bc[(i + 1) % 5]) & bc[(i + 2) % 5];
        }
      }

      // Iota
      q[0] ^= keccakf_rndc[r];
    }

    // Map from long[] to byte[]
    for (int i = 0, j = 0; i < 25; i++, j += 8) {
      // st or q?
      t = q[i];
      v[j + 0] = (byte) ((t >> 0) & 0xFF);
      v[j + 1] = (byte) ((t >> 8) & 0xFF);
      v[j + 2] = (byte) ((t >> 16) & 0xFF);
      v[j + 3] = (byte) ((t >> 24) & 0xFF);
      v[j + 4] = (byte) ((t >> 32) & 0xFF);
      v[j + 5] = (byte) ((t >> 40) & 0xFF);
      v[j + 6] = (byte) ((t >> 48) & 0xFF);
      v[j + 7] = (byte) ((t >> 56) & 0xFF);
    }

  }
  
  
  /************************************************************
   *                       Keccak 				              *
   ************************************************************/

  /**
   * Initialize the context for SHA3 Adapted from
   * https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
   * 
   * @param _mdlen hash output in bytes
   */
  void sha3_init(int _mdlen) {
    st = new byte[200];
    mdlen = _mdlen;
    rsiz = 200 - 2 * mdlen;
    pt = 0;
  }

  /**
   * Update state with more data Adapted from
   * https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
   * 
   * @data data used to update state
   * @param _mdlen hash output in bytes
   */
  void sha3_update(byte[] data, int _mdlen) {
    int j = pt;
    for (int i = 0; i < _mdlen; i++) {
      st[j++] ^= data[i];
      if (j >= rsiz) {
        sha3_keccakf(st);
        j = 0;
      }
    }
    pt = j;
  }

  /**
   * Finalize and output a hash Adapted from
   * https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
   * 
   * @md SHA-3 hash
   */
  void sha3_final(byte[] md) {
    st[pt] ^= 0x06;
    st[rsiz - 1] ^= 0x80;
    sha3_keccakf(st);

    for (int i = 0; i < mdlen; i++)
      md[i] = st[i];
  }

  /**
   * Compute a SHA-3 hash (md) of given byte length Adapted from
   * https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
   * 
   * @param in    input data
   * @param inlen given byte length
   * @param md output buffer
   * @param mdlen hash output in bytes
   */
  void Keccak(byte[] in, int inlen, byte[] md, int mdlen) {
    sha3_init(mdlen);
    sha3_update(in, inlen);
    sha3_final(md);
  }
  
  
  /************************************************************
   *                       SHAKE 				              *
   ************************************************************/

  /**
   * Initialize the context for SHAKE128 Adapted from
   * https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
   */
  void shake128_init() {
    sha3_init(16);
  }

  /**
   * Initialize the context for SHAKE256 Adapted from
   * https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
   */
  void shake256_init() {
    sha3_init(32);
  }
  
  public void shake_xof() {
    st[pt] ^= (byte) 0x1F;
    st[this.rsiz - 1] ^= (byte) 0x80;
    sha3_keccakf(st);
    this.pt = 0;
  }

  public void shake_out(byte[] out, int len) {
    int j = pt;
    for (int i = 0; i < len; i++) {
      if (j >= rsiz) {
        sha3_keccakf(st);
        j = 0;
      }
      out[i] = st[j++];
    }
    pt = j;
  }
  
  
  /************************************************************
   *                       C-SHAKE 				              *
   ************************************************************/
  
  /**
   * Initialize the context for cSHAKE256 
   * @param N 	 function name
   * @param S	 custom string
   */
  void cShake256_init(String N, String S) {
	shake256_init();
	if (!N.equals("") || !S.equals("")) {
		byte[] prefix = bytepad(UtilMethods.concat(encode_string(N.getBytes()), encode_string(S.getBytes())), 136);
		sha3_update(prefix, prefix.length);
	}
  }
  
  public void cShake_xof() {
	    st[pt] ^= (byte) 0x04;
	    st[this.rsiz - 1] ^= (byte) 0x80;
	    sha3_keccakf(st);
	    this.pt = 0;
  }
  
  /**
   * Compute the streamlined cSHAKE256 on input X with output bitlength L, function name N, and custom string S 
   * @param X    input data
   * @param L 	 desired output length in bits
   * @param N 	 function name
   * @param S	 custom string
   * @return 	 disgest
   */
  static byte[] cShake256(byte[] X, int L, String N, String S) {
	byte[] out = new byte[L >>> 3];
	SHA3 sha3 = new SHA3();
	
	sha3.cShake256_init(N, S);
	
	sha3.sha3_update(X, X.length);
	sha3.cShake_xof();
	sha3.shake_out(out, L >>> 3);
	
	return out;
  }

  /************************************************************
   *                       KMAC 				              *
   ************************************************************/
  /**
   * Initialize the context for KMACXOF256
   * @param K     the key
   * @param S     custom string
   */
  void KMACXOF256_init(byte[] K, String S) {
	  byte[] newK = bytepad(encode_string(K), 136);
	  cShake256_init("KMAC", S);
	  sha3_update(newK, newK.length);
  }
  
  public void KMACXOF256_xof() {
	  	sha3_update(right_encode_0, right_encode_0.length);
	    st[pt] ^= (byte) 0x04;
	    st[this.rsiz - 1] ^= (byte) 0x80;
	    sha3_keccakf(st);
	    this.pt = 0;
}
  
  /**
   * Compute the streamlined KMACXOF256 with key K on input X, with output length L and custom string S
   * @param K	 the key
   * @param X    input data
   * @param L 	 desired output length in bits
   * @param S	 custom string
   * @return 	 disgest
   */
  static byte[] KMACXOF256(byte[] K, byte[] X, int L, String S) {
	byte[] out = new byte[L >>> 3];
	SHA3 sha3 = new SHA3();
	
	sha3.KMACXOF256_init(K, S);
	
	sha3.sha3_update(X, X.length);
	
	
	sha3.KMACXOF256_xof();
	sha3.shake_out(out, L >>> 3);
	
	return out;
  }
  
  
  /************************************************************
   *                    Auxiliary Methods                     *
   ************************************************************/ 
  private static byte[] encode_string(byte[] S) {
	  // Validity Conditions: 0 ≤ len(S) < 2^2040
	  int slen = (S != null) ? S.length : 0;
	  byte[] lenS = (S != null) ? left_encode(slen << 3) : left_encode_0; // NB: bitlength, not bytelength
	  byte[] encS = new byte[lenS.length + slen];
	  System.arraycopy(lenS, 0, encS, 0, lenS.length);
	  System.arraycopy((S != null) ? S : encS, 0, encS, lenS.length, slen);
	  return encS; // left_encode(len(S)) || S.
  }
  
  /**
   * Pads a byte string
   * @param X the input string to pad
   * @param w the desired factor of the padding
   * @return a byte array prepended by left_encode(w) such that it's length is a multiple of w
  */
  private static byte[] bytepad(byte[] X, int w) {

      byte[] z = left_encode(w);

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
  private static byte[] left_encode(int x){
	// Validity Conditions: 0 ≤ x < 2^2040
      // 1. Let n be the smallest positive integer for which 2^(8*n) > x.
      int n = 1;
      while ((1 << (8*n)) <= x) {
          n++;
      }
      if (n >= 256) {
          throw new RuntimeException("Left encoding overflow for length " + n);
      }
      // 2. Let x1, x2, ..., xn be the base-256 encoding of x satisfying:
      //    x = Σ 2^(8*(n-i))*x_i, for i = 1 to n.
      // 3. Let Oi = enc8(xi), for i = 1 to n.
      byte[] val = new byte[n + 1];
      for (int i = n; i > 0; i--) {
          val[i] = (byte)(x & 0xFF);
          x >>>= 8;
      }
      // 4. Let O0 = enc8(n).
      val[0] = (byte)n;
      // 5. Return O = O0 || O1 || …|| On−1 || On.
      return val;
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
  private static byte[] right_encode(int c){
    BigInteger x = BigInteger.valueOf(c);
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
  
}
