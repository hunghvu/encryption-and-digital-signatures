import java.math.BigInteger;

public class EncodeString {

    private static final byte[] left_encode_0 = {(byte)0x01, (byte)0x00}; // left_encode(0)

    public static void main (String[] args) {
        // I am not sure how to init to test this yet.
        String decimal = "1061246";
       // byte[] resultLeft = left_encode(new BigInteger(decimal));
//        for (int i = 0; i < resultLeft.length; i++) {
//            // Print byte as raw string: https://stackoverflow.com/questions/12310017/how-to-convert-a-byte-to-its-binary-string-representation
//            String s1 = String.format("%8s", Integer.toBinaryString(resultLeft[i] & 0xFF)).replace(' ', '0');
//            System.out.println("left_encode: " + s1);
//        }
    }

    /**
     * The encode_string function is used to encode bit strings in a way that may be parsed
     * unambiguously from the beginning of the string, S.
     * Validity Conditions: 0 ≤ len(S) < 2^2040
     * @param S String S
     * @return Returns an encoded string.
     */
    private static byte[] encode_string(byte[] S) {
        // Validity Conditions: 0 ≤ len(S) < 2^2040
        int slen = (S != null) ? S.length : 0;
        byte[] lenS = (S != null) ? left_encode(slen << 3) : left_encode_0; // NB: bitlength, not bytelength
        byte[] encS = new byte[lenS.length + slen];
        System.arraycopy(lenS, 0, encS, 0, lenS.length);
        System.arraycopy((S != null) ? S : encS, 0, encS, lenS.length, slen);
        return encS; // left_encode(len(S)) || S.
    }


    private static byte[] left_encode(int x){

        // Validity Conditions: 0 ≤ x < 2^2040
        // 1. Let n be the smallest positive integer for which 2^(8*n) > x.
        int n = 1;
        while ((1 << (8*n)) <= x) {
            n++;
        }
        if (n >= 256) {
            throw new RuntimeException("Left encoding overflow for length " + n);
        }
        // 2. Let x1, x2, ..., xn be the base-256 encoding of x satisfying:
        //    x = Σ 2^(8*(n-i))*x_i, for i = 1 to n.
        // 3. Let Oi = enc8(xi), for i = 1 to n.
        byte[] val = new byte[n + 1];
        for (int i = n; i > 0; i--) {
            val[i] = (byte)(x & 0xFF);
            x >>>= 8;
        }
        // 4. Let O0 = enc8(n).
        val[0] = (byte)n;
        // 5. Return O = O0 || O1 || …|| On−1 || On.
        return val;
    }
}


