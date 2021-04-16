/**
 * Translation file for the Chi for loop from C to Java
 * Referenced at https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
 */
public class Chi {


    private void chi(long[] st) {
        long[] bc = new long[5];

        for (int j = 0; j < 25; j += 5) {
            for (int i = 0; i < 5; i++) {
                bc[i] = st[j + i];
            }
            for (int i = 0; i < 5; i++) {
                st[j + i] ^= (~bc[(i + 1) % 5]) & bc[(i + 2) % 5];
            }
        }
    }
}
