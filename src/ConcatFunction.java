public class ConcatFunction {

    /**
     * Concatenation function for 2 byte arrays. Assist from https://stackoverflow.com/questions/5513152/easy-way-to-concatenate-two-byte-arrays
     * @param a Byte Array a
     * @param b Byte Array b
     * @return A concatenation of a and b as c.
     */
    private static byte[] concatByteArrays(byte[] a, byte[]b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
