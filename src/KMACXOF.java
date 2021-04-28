//public class KMACXOF {
//    public static byte[] KMACXOF256(byte[] K, byte[] X, int L, byte[] S)  {
//        if (L < 0 ) {
//            throw new RuntimeException("Length must not be less than 0");
//        }
//
//        byte[] newX = bytepad(concatByteArrays(concatByteArrays(encode_string(K),X), right_encode(0)), 136);
//        update(newX, newX.length);
//
//        return newX;
//
//    }
//
//}
