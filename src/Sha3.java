
// // sha3.h
// #ifndef SHA3_H
// #define SHA3_H

// #include <stddef.h>
// #include <stdint.h>

// #ifndef KECCAKF_ROUNDS
// #define KECCAKF_ROUNDS 24
// #endif

// #ifndef ROTL64
// #define ROTL64(x, y) (((x) << (y)) | ((x) >> (64 - (y))))
// #endif

// // state context
// typedef struct {
//     union {                                 // state:
//         uint8_t b[200];                     // 8-bit bytes
//         uint64_t q[25];                     // 64-bit words
//     } st;
//     int pt, rsiz, mdlen;                    // these don't overflow
// } sha3_ctx_t;

// // Compression function.
// void sha3_keccakf(uint64_t st[25]);

// // OpenSSL - like interfece
// int sha3_init(sha3_ctx_t *c, int mdlen);    // mdlen = hash output in bytes
// int sha3_update(sha3_ctx_t *c, const void *data, size_t len);
// int sha3_final(void *md, sha3_ctx_t *c);    // digest goes to md

// // compute a sha3 hash (md) of given byte length from "in"
// void *sha3(const void *in, size_t inlen, void *md, int mdlen);

// // SHAKE128 and SHAKE256 extensible-output functions
// #define shake128_init(c) sha3_init(c, 16)
// #define shake256_init(c) sha3_init(c, 32)
// #define shake_update sha3_update

// void shake_xof(sha3_ctx_t *c);
// void shake_out(sha3_ctx_t *c, void *out, size_t len);

// #endif

// #include "sha3.h"

// The source code is heavily based on https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.h

// update the state with given number of rounds

public class Sha3 {

  public long[/* 25 */] st; // st->q
  public int pt, rsiz, mdlen;
  // constants
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

  void sha3_keccakf(long[/*25*/] v) {
    // variables
    int i, j, r;
    long t;
    long[] bc = new long[5];


  // Map from byte[] to long[]
  // j+=8 to go to next bunch of long
  for(i = 0, j = 0; i < 25; i++, j += 8) {
      // st[i], or q[i]?
      st[i] = (((long) v[j + 0] & 0xFFL) << 0 ) | (((long) v[j + 1] & 0xFFL) << 8 ) |
              (((long) v[j + 2] & 0xFFL) << 16) | (((long) v[j + 3] & 0xFFL) << 24) |
              (((long) v[j + 4] & 0xFFL) << 32) | (((long) v[j + 5] & 0xFFL) << 40) |
              (((long) v[j + 6] & 0xFFL) << 48) | (((long) v[j + 7] & 0xFFL) << 56);
  }


  // actual iteration
  for(r=0;r<KECCAKF_ROUNDS;r++) {
    // Theta
    for (int i = 0; i < 5; i++)
      bc[i] = st[i] ^ st[i + 5] ^ st[i + 10] ^ st[i + 15] ^ st[i + 20];

    for (int i = 0; i < 5; i++) {
      long t = bc[(i + 4) % 5] ^ ROTL64(bc[(i + 1) % 5], 1);
      for (int j = 0; j < 25; j += 5)
          st[j + i] ^= t;
    }

    // Rho Pi
    long t = st[1];
    for (int i = 0; i < 24; i++) {
      int j = keccakf_piln[i];
      bc[0] = st[j];
      st[j] = ROTL64(t, keccakf_rotc[i]);
      t = bc[0];
    }

    // Chi
    for (j = 0; j < 25; j += 5) {
      for (i = 0; i < 5; i++)
        bc[i] = st[j + i];
      for (i = 0; i < 5; i++)
        st[j + i] ^= (~bc[(i + 1) % 5]) & bc[(i + 2) % 5];
    }

    for (int j = 0; j < 25; j += 5) {
      for (int i = 0; i < 5; i++) {
          bc[i] = st[j + i];
      }
      for (int i = 0; i < 5; i++) {
          st[j + i] ^= (~bc[(i + 1) % 5]) & bc[(i + 2) % 5];
      }
    }

    // Iota
    st[0] ^= keccakf_rndc[r];
  }


  // Map from long[] to byte[]
  for(i = 0, j = 0; i < 25; i++, j += 8) {
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

