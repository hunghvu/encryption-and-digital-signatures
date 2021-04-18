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

  void sha3_keccakf(long[/*25*/] st) {
    // variables
    int i, j, r;
    long t;
    long[] bc = new long[5];

    #if __BYTE_ORDER__!=__ORDER_LITTLE_ENDIAN__
      uint8_t*v;

  // endianess conversion. this is redundant on little-endian targets
  for(i=0;i<25;i++) {
    v = (uint8_t *) &st[i];
      st[i] = ((uint64_t) v[0]) | (((uint64_t) v[1]) << 8) |
            (((uint64_t) v[2]) << 16) | (((uint64_t) v[3]) << 24) |
            (((uint64_t) v[4]) << 32) | (((uint64_t) v[5]) << 40) |
            (((uint64_t) v[6]) << 48) | (((uint64_t) v[7]) << 56);
  }
    // #endif

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

  #if __BYTE_ORDER__!=__ORDER_LITTLE_ENDIAN__
  // endianess conversion. this is redundant on little-endian targets
  for(i=0;i<25;i++)
  {
        v = (uint8_t *) &st[i];
        t = st[i];
        v[0] = t & 0xFF;
        v[1] = (t >> 8) & 0xFF;
        v[2] = (t >> 16) & 0xFF;
        v[3] = (t >> 24) & 0xFF;
        v[4] = (t >> 32) & 0xFF;
        v[5] = (t >> 40) & 0xFF;
        v[6] = (t >> 48) & 0xFF;
        v[7] = (t >> 56) & 0xFF;
    }#endif
}


}
