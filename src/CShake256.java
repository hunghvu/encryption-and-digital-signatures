//public class CShake256 {
//
//    public static byte[] cSHAKE256(byte[] X, int L, byte[] N, byte[] S) {
//        // Validity Conditions: len(N) < 2^2040 and len(S) < 2^2040
//
//        if (N.length == 0 && S.length == 0) {
//            return SHAKE256(X, L);
//        } else {
//            ADD
//            byte[] val = bytepad(concat(encode_string(N), encode_string(S)), 136);
//            update(val, val.length);
//        }
//        return val;
//    }
//}
