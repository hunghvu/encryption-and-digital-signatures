

public class Sha3 {

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
  
  public Sha3() {
	  
  }

  /**
   * Rotate the 64-bit long value x by y positions to the left
   * Adapted from https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.h
   * @param   x any 64-bit long value
   * @param   y the left-rotation displacement
   * @return  the 64-bit long value x left-rotated by y positions
   */
  private static long ROTL64(long x, int y) {
      return (x << y) | (x >>> (64 - y));
  }

  /**
   * The Keccack-p permutation, ref section 3.3 NIST FIPS 202.
   * Adapted from https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
   * @param stateIn the input state array
   * @return the state after the Keccak-p permutation has been applied
   */
 private void sha3_keccakf(byte[/*200*/] v) {
    // variables
    long t;
    long[] q = new long[25];
    long[] bc = new long[5];


  // Map from byte[] to long[]
  // j+=8 to go to next bunch of long
  for(int i = 0, j = 0; i < 25; i++, j += 8) {
      // st[i], or q[i]?
      q[i] = (((long) v[j + 0] & 0xFFL) << 0 ) | (((long) v[j + 1] & 0xFFL) << 8 ) |
              (((long) v[j + 2] & 0xFFL) << 16) | (((long) v[j + 3] & 0xFFL) << 24) |
              (((long) v[j + 4] & 0xFFL) << 32) | (((long) v[j + 5] & 0xFFL) << 40) |
              (((long) v[j + 6] & 0xFFL) << 48) | (((long) v[j + 7] & 0xFFL) << 56);
  }


  // actual iteration
  for(int r=0; r<KECCAKF_ROUNDS; r++) {
    // Theta
    for (int i = 0; i < 5; i++)
      bc[i] = st[i] ^ st[i + 5] ^ st[i + 10] ^ st[i + 15] ^ st[i + 20];

    for (int i = 0; i < 5; i++) {
      t = bc[(i + 4) % 5] ^ ROTL64(bc[(i + 1) % 5], 1);
      for (int j = 0; j < 25; j += 5)
          st[j + i] ^= t;
    }

    // Rho Pi
    t = st[1];
    for (int i = 0; i < 24; i++) {
      int j = keccakf_piln[i];
      bc[0] = st[j];
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
  for(int i = 0, j = 0; i < 25; i++, j += 8) {
        // st or q?
        t = st[i];
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

   
  /**
   * Initialize the context for SHA3
   * Adapted from https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
   * @param _mdlen hash output in bytes
   */
   void sha3_init(int _mdlen) {
      for (int i = 0; i < 200; i++)
         st[i] = (byte)0;
      mdlen = _mdlen;
      rsiz = 200 - 2*mdlen;
      pt = 0;
   }
   
   /**
    * Initialize the context for SHAKE128
    * Adapted from https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
    */
    void shake128_init() {
    	sha3_init(16);
    }
    
    /**
     * Initialize the context for SHAKE256
     * Adapted from https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
     */
     void shake256_init() {
     	sha3_init(32);
     }
   
  /**
   * Update state with more data
   * Adapted from https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
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
   * Finalize and output a hash
   * Adapted from https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
   * @md SHA-3 hash
   */
   byte[] sha3_final(byte[] md) {
      st[pt] ^= 0x06;
      st[rsiz - 1] ^= 0x80;
      sha3_keccakf(st);
      
      for (int i = 0; i < mdlen; i++)
         md[i] = st[i];
      
      return md;
   }
   
   /**
    * Compute a SHA-3 hash (md) of given byte length
    * Adapted from https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
    * @param in input data
    * @param inlen given byte length
    * @param mdlen hash output in bytes
    */
    byte[] Keccak(byte[] in, int inlen, int mdlen) {
    	sha3_init(mdlen);
    	sha3_update(in, inlen);
    	return sha3_final(new byte[64]);
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
   
   
}

