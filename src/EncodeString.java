//public class EncodeString {
//   // I am not sure how to init to test this yet.
//
//}

    /**
     * The encode_string function is used to encode bit strings in a way that may be parsed
     * unambiguously from the beginning of the string, S.
     * Validity Conditions: 0 ≤ len(S) < 2^2040
     * @param S String S
     * @return Returns an encoded string.
     */
//    private static byte[] encode_string(byte[] S) {
//        // Validity Conditions: 0 ≤ len(S) < 2^2040
//        int slen = (S != null) ? S.length : 0;
//        byte[] lenS = (S != null) ? left_encode(slen << 3) : left_encode_0; // NB: bitlength, not bytelength
//        byte[] encS = new byte[lenS.length + slen];
//        System.arraycopy(lenS, 0, encS, 0, lenS.length);
//        System.arraycopy((S != null) ? S : encS, 0, encS, lenS.length, slen);
//        return encS; // left_encode(len(S)) || S.
//    }
//}

