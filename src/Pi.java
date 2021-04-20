package src;
public class Pi {
  private static final int[] keccakf_rotc={
        1,  3,  6,  10, 15, 21, 28, 36, 45, 55, 2,  14,
        27, 41, 56, 8,  25, 43, 62, 18, 39, 61, 20, 44
  };
  private static final int[] keccakf_piln= {
        10, 7,  11, 17, 18, 3, 5,  16, 8,  21, 24, 4,
        15, 23, 19, 13, 12, 2, 20, 14, 22, 9,  6,  1
  };
  //unsigned int to long for t.
  // public void pi() {
  //   long t = st[1];
  //   for (int i = 0; i < 24; i++) {
  //     int j = keccakf_piln[i];
  //     bc[0] = st[j];
  //     st[j] = ROTL64(t, keccakf_rotc[i]);
  //     t = bc[0];
  //   }
  // }
}
