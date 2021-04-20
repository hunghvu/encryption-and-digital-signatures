public class Sha3 {
   
  /**
   * Initialize the context for SHA3
   * Adapted from https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
   * @param _mdlen hash output in bytes
   */
   void sha3_init(int _mdlen) {
      for (int i = 0; i < 200; i++)
         st[i] = (byte)0;
      mdlen = _mlen;
      rsiz = 200 - 2*mdlen;
      pt = 0;
   }
   
  /**
   * Update state with more data
   * Adapted from https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
   * @data data used to update state
   * @param _mdlen hash output in bytes
   */
   void sha3_update(byte[] data, int _mdlen) {
      int j = pt;
      for (int i = 0; i < len; i++) {
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
   
   
}