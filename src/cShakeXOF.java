//public class cShakeXOF {
//    /**
//     * Referenced to Paulo Barreto and https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c#L15
//     */
//    public void cShakeXOF() {
//        if (kmac) {
//            update(right_encode_0, right_encode_0.length); // mandatory padding as per the NIST specification
//        }
//        // the (binary) cSHAKE suffix is 00, while the (binary) Model.SHAKE suffix is 1111
//        this.b[this.pt] ^= (byte)(0x04);
//        this.b[this.rsiz - 1] ^= (byte)0x80;
//        sha3_keccakf(b);
//        this.pt = 0;
//    }
//}
